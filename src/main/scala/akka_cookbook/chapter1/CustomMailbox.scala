package akka_cookbook.chapter1

import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.dispatch.{Envelope, MailboxType, MessageQueue, ProducesMessageQueue}
import com.typesafe.config.Config

/**
  * Created by xiangnanren on 03/10/2017.
  */
object CustomMailbox extends App {
  val actorSystem = ActorSystem("HelloAkka")
  val specialActor = actorSystem.actorOf(Props[MySpecialActor].
    withDispatcher("custom-dispatcher"))

  val actor1 = actorSystem.actorOf(Props[MyActor], "xyz")
  val actor2 = actorSystem.actorOf(Props[MyActor], "MyActor")

  actor1 ! ("hello", specialActor)
  actor2 ! ("hello", specialActor)

}


class MySpecialActor extends Actor {
  override def receive: Receive = {
    case msg: String => println(s"msg is $msg")
  }
}

class MyActor extends Actor {
  override def receive: Receive = {
    case (msg: String, actorRef: ActorRef) =>
      actorRef ! msg
    case msg => println(msg)
  }
}


class MyMessageQueue extends MessageQueue {
  private final val queue = new ConcurrentLinkedQueue[Envelope]()

  override def enqueue(receiver: ActorRef,
                       handle: Envelope): Unit = {
    if (handle.sender.path.name == "MyActor") {
      handle.sender ! "Hey dude, How are you?, " +
        "I know your name, processing your request"
      queue.offer(handle)
    }
    else handle.sender ! "I don't talk to strangers, " +
      "I can't process your request"
  }

  override def dequeue(): Envelope = queue.poll()

  override def numberOfMessages: Int = queue.size()

  override def hasMessages: Boolean = !queue.isEmpty

  override def cleanUp(owner: ActorRef, deadLetters: MessageQueue): Unit = {
    while (hasMessages) {
      deadLetters.enqueue(owner, dequeue())
    }
  }
}


class MyUnboundedMailbox extends MailboxType
  with ProducesMessageQueue[MyMessageQueue] {
  def this(settings: ActorSystem.Settings, config: Config) = this()

  override def create(owner: Option[ActorRef],
                      system: Option[ActorSystem]): MessageQueue = {
    new MyMessageQueue()
  }
}











