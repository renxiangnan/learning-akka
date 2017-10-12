package akka_cookbook.chapter2_supervision.supervisor_strategy_all_1

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor._

import scala.concurrent.duration._

/**
  * Created by xiangnanren on 05/10/2017.
  */
object AllForOneStrategyApp extends App {
  val system = ActorSystem("Hello-Akka")
  val supervisor = system.actorOf(Props[AllForOneStrategySupervisor], "supervisor")
  supervisor ! "Start"
}

case class Add(a: Int, b: Int)
case class Sub(a: Int, b: Int)
case class Div(a: Int, b: Int)

class Calculator(printer: ActorRef) extends Actor {

  override def preRestart(reason: Throwable,
                          message: Option[Any]): Unit = {
    println("Calculator: I am restarting because of ArithmeticException")
  }

  override def receive: Receive = {
    case Add(a, b) => printer ! s"sum is ${a + b}"
    case Sub(a, b) => printer ! s"diff is ${a - b}"
    case Div(a, b) => printer ! s"div is ${a/b}"
  }
}

class ResultPrinter extends Actor {
  override def preRestart(reason: Throwable,
                        message: Option[Any]): Unit =
    println("Printer: I am restarting as well")

  override def receive: Receive = {
    case msg => println(msg)
  }
}

class AllForOneStrategySupervisor extends Actor {
  override val supervisorStrategy =
    AllForOneStrategy(
      maxNrOfRetries = 10,
      withinTimeRange = 1.seconds) {
      case _: ArithmeticException => Restart
      case _: NullPointerException => Resume
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }

  val printer = context.actorOf(Props[ResultPrinter])
  val calculator =
    context.actorOf(Props(classOf[Calculator], printer))

  override def receive: Receive = {
    case "Start" =>
      calculator ! Add(10, 12)
      calculator ! Sub(12, 10)
      calculator ! Div(5, 2)
      calculator ! Div(5, 0)
  }
}

