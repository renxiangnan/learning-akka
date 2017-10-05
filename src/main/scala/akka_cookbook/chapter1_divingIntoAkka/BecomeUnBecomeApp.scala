package akka_cookbook.chapter1_divingIntoAkka

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by xiangnanren on 03/10/2017.
  */
object BecomeUnBecomeApp extends App {
  val actorSystem = ActorSystem("HelloAkka")
  val becomeUnBecome = actorSystem.actorOf(Props[BecomeUnBecomeActor])

  becomeUnBecome ! true
  becomeUnBecome ! "Hello how are you"
  becomeUnBecome ! false
  becomeUnBecome ! 1100
  becomeUnBecome ! true
  becomeUnBecome ! "What do u you"
}

class BecomeUnBecomeActor extends Actor {
  def receive: Receive = {
    case true => context.become(isStateTrue)
  }

  def isStateTrue: Receive = {
    case msg: String => println(s"State true $msg")
    case false => context.become(isStateFalse)
  }

  def isStateFalse: Receive = {
    case msg: Int => println(s"State false: $msg")
    case true => context.become(isStateTrue)
  }
}