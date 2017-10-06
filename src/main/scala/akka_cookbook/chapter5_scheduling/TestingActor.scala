package akka_cookbook.chapter5_scheduling

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}


/**
  * Created by xiangnanren on 05/10/2017.
  */
class TestingActor extends TestKit(ActorSystem("TestSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Sum actor" must {
    "send back sum of two integers" in {
      val sumActor = system.actorOf(Props[SumActor])
      sumActor ! (10, 12)
      expectMsg(22)
    }
  }
}

class SumActor extends Actor {
  def receive: Receive = {
    case (a: Int, b: Int) => sender ! (a + b)
  }
}