package akka_cookbook.chapter8_stream.sec1_simple_stream_app

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

/**
  * @author Xiangnan Ren
  */
object SimpleStreamsApplication extends App {

  /**
    * There are 3 main stages in Akka Streams: sources, flows, and sinks .
    *
    *      Source:      something with exactly one output stream
    *      Sink:        something with exactly one input stream
    *      Flow:        something with exactly one input and one output stream
    *      BidiFlow:    something with exactly two input streams and two output streams
    *                   that conceptually behave like two Flows of opposite direction
    *                   
    *      Graph:       a packaged stream processing topology that exposes a certain set of input and output ports,
    *                   characterized by an object of type Shape.
    *
    *      Shape:       A Shape describes the inlets and outlets of a Graph. In keeping with the
    *                   philosophy that a Graph is a freely reusable blueprint, everything that
    *                   matters from the outside are the connections that can be made with it,
    *                   otherwise it is just a black box.
    *
    *
    * {{{ActorMaterializer}}} is responsible for creating the underlying actors with
    * the specific functionality you define in your stream. Since ActorMaterializer
    * creates actors, it also needs an ActorSystem. This is the reason why we implicitly
    * defined one in our sample code.
    */
  implicit val actorSystem: ActorSystem = ActorSystem("SimpleStream")
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()

  val fileList = List("src/main/resources/stream/testfile1.txt",
    "src/main/resources/stream/testfile2.txt",
    "src/main/resources/stream/testfile3.txt")

  val stream = Source(fileList).
    map(new java.io.File(_)).
    filter(_.exists()).
    filter(_.length() != 0).
    to(Sink.foreach(f => println(s"Absolute path: ${f.getAbsolutePath}")))

  stream.run()
}
