package akka_cookbook.chapter8_stream.graph

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Balance, Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import scala.concurrent.duration._

/**
  * @author Xiangnan Ren
  */
object WorkingWithGraphApp extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("WorkingWithGraphs")
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()

  val graph = RunnableGraph.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      /**
        * Emit elements periodically
        */
      val androidNotification = Source.tick(100.millis, 200.millis, new AndroidMsg)
      val iOSNotification = Source.tick(150.millis, 100.millis, new IosMsg)

      /**
        * Define Flow for map, group, count
        */
      val groupAndroid = Flow[AndroidMsg].map(_.toGenMsg("ANDROID")).groupedWithin(5, 5.seconds).async
      val groupIos = Flow[IosMsg].map(_.toGenMsg("IOS")).groupedWithin(5, 5.seconds).async
      val counter = Flow[Seq[GenericMsg]].via(new StatefulCounterFlow())
      val mapper = Flow[Seq[GenericMsg]].mapConcat(_.toList)

      // 2 output ports
      val aBroadcast = builder.add(Broadcast[Seq[GenericMsg]](2))
      val iBroadcast = builder.add(Balance[Seq[GenericMsg]](2))

      val balancer = builder.add(Balance[Seq[GenericMsg]](2))
      val notificationMerge = builder.add(Merge[Seq[GenericMsg]](2))
      val genericNotificationMerge = builder.add(Merge[GenericMsg](2))

      def counterSink(s: String) = Sink.foreach[Int](x => println(s"$s: [$x]"))

      /**
        * Construction of graph
        */
      androidNotification ~> groupAndroid ~> aBroadcast ~> counter ~> counterSink("Android")
      aBroadcast ~> notificationMerge

      iBroadcast ~> notificationMerge
      iOSNotification ~> groupIos ~> iBroadcast ~> counter ~> counterSink("Ios")


      /**
        *
        * notificationMerge ---->           ----> mapper.async ---->
        *                   |      balancer |                  |     genericNotificationMerge ---> Sink
        *                   |--->           |---> mapper.async |--->
        */
      notificationMerge ~> balancer ~> mapper.async ~> genericNotificationMerge
      balancer ~> mapper.async ~> genericNotificationMerge

      genericNotificationMerge ~> Sink.foreach(println)

      ClosedShape
    })
  graph.run()
}
