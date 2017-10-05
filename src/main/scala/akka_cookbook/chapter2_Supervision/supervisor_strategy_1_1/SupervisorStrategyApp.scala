package akka_cookbook.chapter2_supervision.supervisor_strategy_1_1

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorSystem, OneForOneStrategy, Props}

/**
  * Created by xiangnanren on 05/10/2017.
  */
object SupervisorStrategyApp extends App {
  val actorSystem = ActorSystem("Supervision")
  actorSystem.actorOf(Props[SupervisorStrategy]) ! "Start"
}

class Printer extends Actor {
  override def preRestart(reason: Throwable,
                          message: Option[Any]): Unit =
    println("Printer: I am restarting because of ArithmeticException")

  override def receive: Receive = {
    case msg: String => println(s"Printer $msg")
    case msg: Int => 1/0
  }
}

class IntAdder extends Actor {
  var x = 0

  def receive = {
    case msg: Int => x = x + msg
      println(s"IntAdder: sum is $x")
    case msg: String => throw new IllegalArgumentException
  }

  override def postStop = println("IntAdder: I am getting stopped " +
      "because I got a string message")
}

class SupervisorStrategy extends Actor {
  import scala.concurrent.duration._
  override val supervisorStrategy =
    OneForOneStrategy(
      maxNrOfRetries = 10,
      withinTimeRange = 1.minute) {
    case _: ArithmeticException => Restart
    case _: NullPointerException => Resume
    case _: IllegalArgumentException => Stop
    case _: Exception => Escalate
  }

  val printer = context.actorOf(Props[Printer])
  val intAdder = context.actorOf(Props[IntAdder])

  def receive = {
    case "Start" => printer ! "Hello printer"
      printer ! 10
      intAdder ! 10
      intAdder ! 10
      intAdder ! "Hello int adder"
  }
}












