package akka_cookbook.chapter8_stream.example_actor_integration

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.{ActorMaterializer, KillSwitches}
import akka.stream.scaladsl.{Keep, Sink, Source}

import scala.concurrent.duration._

/**
  * @author Xiangnan Ren
  */
class PrintMoreNumbers(implicit materializer: ActorMaterializer)
  extends Actor {
  private implicit val executionContext = context.system.dispatcher

  private val (killSwitch, done) =
    Source.tick(0.seconds, 1.seconds, 1).
      scan(0)(_+_).
      map(_.toString).
      viaMat(KillSwitches.single)(Keep.right).
      toMat(Sink.foreach(println))(Keep.both). // connect to a sink
      run()

  done.map(_ => self ! "done")

  override def receive: Receive = {
    case "stop" =>
      println("Stopping")
      killSwitch.shutdown()
    case "done" =>
      println("Done")
      context.stop(self)
  }
}

object PrintMoreNumbersApp extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val actorRef = system.actorOf(Props(classOf[PrintMoreNumbers], materializer))
  system.scheduler.scheduleOnce(5.seconds) {
    actorRef ! "stop"
  }
}
