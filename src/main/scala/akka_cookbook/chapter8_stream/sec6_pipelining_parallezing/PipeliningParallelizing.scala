package akka_cookbook.chapter8_stream.sec6_pipelining_parallezing

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, FlowShape, UniformFanInShape, UniformFanOutShape}
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge, Sink, Source}

import scala.collection.immutable
import scala.util.Random

/**
  * @author Xiangnan Ren
  */
trait PipeliningParallelizing extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("PipeliningParallelizing")
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
  val tasks: immutable.Seq[Wash] = (1 to 5).map(Wash) // 5 wash-dry tasks to process

  def washStage: Flow[Wash, Dry, NotUsed] = Flow[Wash].
    map(wash => {
      val sleepTime = Random.nextInt(3) * 1000
      println(s"Washing ${wash.id}. It will take $sleepTime milliseconds.")
      Thread.sleep(sleepTime)

      Dry(wash.id)
    })

  def dryStage: Flow[Dry, Done, NotUsed] = Flow[Dry].
    map(dry => {
      val sleepTime = Random.nextInt(3) * 1000
      println(s"Drying ${dry.id}. It will take $sleepTime milliseconds.")
      Thread.sleep(sleepTime)

      Done(dry.id)
    })

  val parallelStage = Flow.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      // 3 output ports
      val dispatchLaundry: UniformFanOutShape[Wash, Wash] = builder.add(Balance[Wash](3))
      val mergeLaundry: UniformFanInShape[Done, Done] = builder.add(Merge[Done](3))

      dispatchLaundry.out(0) ~> washStage.async ~> dryStage.async ~> mergeLaundry.in(0)
      dispatchLaundry.out(1) ~> washStage.async ~> dryStage.async ~> mergeLaundry.in(1)
      dispatchLaundry.out(2) ~> washStage.async ~> dryStage.async ~> mergeLaundry.in(2)

      FlowShape(dispatchLaundry.in, mergeLaundry.out)
    })

  def runGraph(testingFlow: Flow[Wash, Done, NotUsed]) =
    Source(tasks).via(testingFlow).to(Sink.foreach(println)).run()
}

case class Wash(id: Int)
case class Dry(id: Int)
case class Done(id: Int)