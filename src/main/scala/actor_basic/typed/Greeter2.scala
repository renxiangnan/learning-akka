package actor_basic.typed

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.io.StdIn

object Greeter2 {

  case object Greet
  final case class WhoToGreet(who: String)
}

class Greeter2 extends Actor {
  import Greeter2._

  override def receive: Receive = onMessage(currentGreeting = "hello")

  def onMessage(currentGreeting: String): Receive = {
    case WhoToGreet(who) =>
      context.become(onMessage(currentGreeting = s"hello, $who"))
    case Greet =>
      println(currentGreeting)
  }
}

object HelloWorldApp2 {
  import Greeter2._
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("HelloWorld")
    try {
      val greeter: ActorRef = system.actorOf(Props[Greeter2], "greeter")
      greeter ! WhoToGreet("World")
      greeter ! Greet

      println("Press ENTER to exit the system")
      StdIn.readLine()
    } finally {
      system.terminate()
    }
  }
}
