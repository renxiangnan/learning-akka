package actor_basic

import akka.actor.{ActorSystem, Props}

/**
  * Created by xiangnanren on 23/08/2017.
  */
object Main extends App {
  // an actor needs an ActorSystem
  val system = ActorSystem("HelloSystem")

  // create (at ActorSystem level) and start the actor
  // actorOf returns a reference: ActorRef, which could be
  // considered as a broker between you and the actual actor
  val helloActor = system.actorOf(Props[HelloActor], name = "hello-actor")
  val actorWithArgs = system.actorOf(Props(new ActorWithArgs("Fred")), name = "helloactor")

  // send the actor two messages
  helloActor ! "hello my friend"
  helloActor ! "buenos dias"
  actorWithArgs ! "hello"
  actorWithArgs ! "buenos dias"

  // shut down the system
  system.terminate()
}
