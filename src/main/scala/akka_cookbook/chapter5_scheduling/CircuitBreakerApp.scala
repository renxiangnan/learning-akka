package akka_cookbook.chapter5_scheduling

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.{CircuitBreaker, ask}
import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Created by xiangnanren on 05/10/2017.
  */
object CircuitBreakerApp extends App {
  val system = ActorSystem("hello-Akka")
  implicit val ec = system.dispatcher
  implicit val timeout = Timeout(3.seconds)

  val breaker = new CircuitBreaker(system.scheduler,
    maxFailures = 3,
    callTimeout = 1.seconds,
    resetTimeout = 2.seconds).
    onOpen(println("======= State is open =======")).
    onOpen(println("======= State is closed ======="))

  val db = system.actorOf(Props[DBActor], "DBActor")

  (1 to 10).foreach{recordId =>
    Thread.sleep(3000)
    val askFuture = breaker.withCircuitBreaker(
      db ? FetchRecord(recordId))

    askFuture.map(record => s"Record is: $record and RecordID $recordId").
      recover({
        case fail => "Failed with: " + fail.toString
      }).foreach(x => println(x))
  }
}

case class FetchRecord(recordID: Int)
case class Person(name: String, age: Int)

object DB {
  val data = Map(
    1 -> Person("John", 21),
    2 -> Person("Peter", 30),
    3 -> Person("James", 40),
    4 -> Person("Alice", 25),
    5 -> Person("Henry", 26),
    6 -> Person("Jackson", 48))
}

class DBActor extends Actor {
  def receive = {
    case FetchRecord(recordID) =>
      if (recordID >= 3 && recordID <= 5)
        Thread.sleep(3000)
      else sender ! DB.data.get(recordID)
  }
}






