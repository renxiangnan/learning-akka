package akka_cookbook.chapter8_stream.example_actor_integration

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.impl.StreamSupervisor.Materialize
import akka.stream.scaladsl.Source

/**
  * @author Xiangnan Ren
  */
class PrintSomeNumbers(implicit materialize: ActorMaterializer)
 extends Actor {
  private implicit val executionContext = context.system.dispatcher

  Source(1 to 10).
    map(_.toString).
    runForeach(println). // Return Future[Done]
    map(_ => self ! "done")

  override def receive: Receive = {
    case "done" =>
      println("Done")
      context.stop(self)
  }
}

object TrivialExample extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  system.actorOf(Props(classOf[PrintSomeNumbers], materializer))


}