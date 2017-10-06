package actor_basic.typed

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.io.StdIn

/**
  * Created by xiangnanren on 02/10/2017.
  */

class Greeter1 extends Actor {
  import Greeter1._

  private var greeting = "hello"

  override def receive: Receive = {
    case WhoToGreet(who) =>
      greeting = s"hello, $who"
    case Greet =>
      println(greeting)
  }
}

object Greeter1 {
  case object Greet
  final case class WhoToGreet(who: String)
}


object HelloWorldApp1 {
  import Greeter1._
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("HelloWorld")

    try {
      val greeter: ActorRef = system.actorOf(Props[Greeter1], "greeter")
      greeter ! WhoToGreet("World")
      greeter ! Greet

      println("Press ENTER to exit the system")
      StdIn.readLine()
    } finally {
      system.terminate()
    }
  }
}
