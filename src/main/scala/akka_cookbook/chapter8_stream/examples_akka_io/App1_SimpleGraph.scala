package akka_cookbook.chapter8_stream.examples_akka_io
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}

/**
  * @author Xiangnan Ren
  */
object SimpleGraph {

  /**
    *  in --- f1 ---> bcast --- f2 --->
    *                      |            merge --- f3 ---> out
    *                      |--- f4 --->
    */
  val g = RunnableGraph.fromGraph(GraphDSL.create(){ implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val data = List("Hello", "Akka", "Stream")

    // Use Soure.single(collection) to pass a single collection as input
    val in = Source(data)
    val out = Sink.ignore

    /**
      * Junctions
      */
    val bcast = builder.add(Broadcast[String](2))
    val merge = builder.add(Merge[String](2))

    val f1 = Flow[String].map { s =>
      println(s + " [in f1]")
      s
    }
    val f2 = Flow[String].map { s =>
      println(s + " [in f2]")
      s
    }
    val f3 = Flow[String].map { s =>
      println(s + " [in f3]")
      s
    }
    val f4 = Flow[String].map { s =>
      println(s + " [in f4]")
      s
    }

    in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
                bcast ~> f4 ~> merge
    ClosedShape
  })
}

object SimpleGraphLauncher extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("SimpleGraph")
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
  SimpleGraph.g.run()
}