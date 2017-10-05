package akka_cookbook.chapter2_supervision.actor_life_cycle

import akka.actor.SupervisorStrategy._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._


/**
  * Created by xiangnanren on 04/10/2017.
  */

object ActorLifeCycle extends App {
  implicit val timeout = Timeout(2.seconds)
  val actorSystem = ActorSystem("Supervision")
  val supervisor = actorSystem.actorOf(Props[Supervisor], "supervisor")
  val childFuture = supervisor ? (Props(new LifecycleActor), "LifecycleActor")
  val child = Await.result(childFuture.mapTo[ActorRef], 2.seconds)

  child ! Error
  Thread.sleep(1000)
  supervisor ! StopActor(child)

}

object ActorLifeCycle1 extends App {
  implicit val timeout = Timeout(2.seconds)
  val actorSystem = ActorSystem("Supervision")
  val supervisor = actorSystem.actorOf(Props[Supervisor], "supervisor")

  val childFuture = supervisor ? (Props(new LifecycleActor), "LifecycleActor")
  val child = Await.result(childFuture.mapTo[ActorRef], 2.seconds)

  child ! Error
  Thread.sleep(1000)
  supervisor ! StopActor(child)

}

case object Error
case class StopActor(actor: ActorRef)

class LifecycleActor extends Actor {
  var sum = 1
  override def preRestart(reason: Throwable,
                          message: Option[Any]): Unit = {
    println(s"sum in preRestart is $sum")
  }

  override def preStart(): Unit = println(
    s"sum in preStart is $sum"
  )

  override def receive: Receive = {
    case Error => throw new ArithmeticException()
    case _ => println("default msg")
  }

  override def postStop(): Unit = {
    println(s"sum in postStop is ${sum * 3}")
  }

  override def postRestart(reason: Throwable): Unit = {
    sum = sum * 2
    println(s"sum in postRestart is $sum")
  }
}

class Supervisor extends Actor {
  override val supervisorStrategy = OneForOneStrategy(
    maxNrOfRetries = 10,
    withinTimeRange = 1.minute){
    case _: ArithmeticException => Restart

    //  Escalates the failure to the supervisor of the supervisor,
    //  by rethrowing the cause of the failure, i.e. the supervisor fails with
    //  the same exception as the child.
    case t => super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
  }

  override def receive: Receive = {
    case (props: Props, name: String) =>
      sender() ! context.actorOf(props, name)
    case StopActor(actorRef) => context.stop(actorRef)
  }
}



