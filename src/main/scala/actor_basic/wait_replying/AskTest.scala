package actor_basic.wait_replying

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import akka.pattern.ask

/**
  * Created by xiangnanren on 01/10/2017.
  */

/**
  * Send a message to an actor using either ? or ask instead of the usual ! method.
  *
  *  The ? and ask methods create a Future, so you use Await.result to wait for the
  *  response from the other actor.
  *
  *  ! means “fire-and-forget”, returns reference of an actor,
  *  e.g. send a message asynchronously and return immediately. Also known as tell
  *  ? returns the future, sends a message asynchronously and
  *  returns a Future representing a possible reply. Also known as ask.
  */
object AskTest extends App {
  val system = ActorSystem("AskTestSystem")
  val myActor = system.actorOf(Props[TestActor], name = "myActor")

  // (1) this is one way to "ask" another actor for information
  implicit val timeout = Timeout(5 seconds)

  val future1 = myActor ? AskNameMessage
  val result1 = Await.result(future1, timeout.duration).asInstanceOf[String]
  println(result1)

  val future2: Future[String] = ask(myActor, AskNameMessage).mapTo[String]
  val result2 = Await.result(future2, 1 second)
  println(result2)


}

case object AskNameMessage

class TestActor extends Actor {
  def receive = {
    case AskNameMessage => sender() ! "Fred"
    case _ => println("that was unexpected")
  }
}
