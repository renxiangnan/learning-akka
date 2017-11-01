package akka_cookbook.chapter8_stream.sec2_transform_consume

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.Future

/**
  * @author Xiangnan Ren
  */
object TransformingStreamApp extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("TransformingStream")
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
  val MaxGroups = 100

  val path = Paths.get("src/main/resources/stream/gzipped-file.gz")


  /**
    * 1. Uncompress the file to read the contents.
    *     - To do so, use helper Flow, i.e., Compression.gunzip() .
    *     - The Flow object is provided as an intermediate processing stage using the method via.
    *     - via expects a Flow and allows you to modularize you streams.
    *
    * 2. Map ByteStrings to UTF8 strings.
    *
    * 3. Turn string to multiple elements. MapConcat takes one element and outputs from
    *    0 to n elements. Here, we split the line by spaces. mapConcat is semantically
    *    similar to flatMap in Scala.
    *
    * 4. Collect each non-empty element and replaced all the new lines and punctuation signs.
    *
    * 5. Group the elements by using groupBy method. groupBy demultiplexes the incoming stream
    *    into separate output streams, one for each element key.
    *    BE NOT THAT, groupBy does not permit unlimited output substreams. Use MaxGroups to
    *    provide a max substream value.
    *
    * 6. Use mergeSubstreams to flatten the substream into by performing a merge operation.
    *
    *
    */
  val stream = transform(FileIO.fromPath(path))



  def transform(input: Source[ByteString, Future[IOResult]]):
  RunnableGraph[Future[IOResult]] = {

    /**
      * mapConcat:  Transform each input element into an `Iterable` of
      *             output elements that is then flattened into the output stream.
      *
      * collect:  Transform this stream by applying the given partial function to each of the elements
      *           on which the function is defined as they pass through this processing step.
      *           Non-matching elements are filtered out.
      *
      * groupBy:  This operation demultiplexes the incoming stream into separate output streams,
      *           one for each element key. The key is computed for each element using the given function.
      *           When a new key is encountered for the first time a new substream is opened and subsequently
      *           fed with all elements belonging to that key.
      */
    input.via(Compression.gunzip()).
      map(_.utf8String.toUpperCase).
      mapConcat(_.split(" ").toList).
      collect { case w if w.nonEmpty =>
        w.replaceAll("\\p{Punct}", "").
          replaceAll(System.lineSeparator(), "")
      }.
      groupBy(MaxGroups, identity).
      map(_ -> 1).
      reduce((l, r) => (l._1, l._2 + r._2)).
      mergeSubstreams.
      to(Sink.foreach(println))
  }


  stream.run()

}
