package actor_basic

import akka.actor.Actor

/**
  * Created by xiangnanren on 23/08/2017.
  */

class ActorWithArgs (myName: String) extends Actor {
  def receive = {
    // (2) println statements changed to show the name
  case "hello" => println(s"hello from $myName")
  case _ => println(s"'huh?', said $myName") }
}

