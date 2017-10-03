package actor_basic


import akka.actor.Actor


/**
  * Created by xiangnanren on 23/08/2017.
  */

// Define an actor
class HelloActor extends Actor {
  override def receive = {
    case "hello my friend" => println("hello back at you")
    case _ => println("huh?") }
}

