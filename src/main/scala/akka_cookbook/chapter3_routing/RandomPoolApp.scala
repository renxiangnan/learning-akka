package akka_cookbook.chapter3_routing

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.RandomPool

/**
  * Created by xiangnanren on 05/10/2017.
  */
object RandomPoolApp extends App {
  val actorSystem = ActorSystem("Hello-Akka")
  val router = actorSystem.actorOf(RandomPool(5).props(Props[RandomPoolActor]), name = "Fat-Panda")
  println(router.path.name)
  for (i <- 1 to 5) {
    router ! s" Hello ${router.path.name}"
  }
}


class RandomPoolActor extends Actor {
  override def receive: Receive = {
    case msg: String =>
      // path name: akka://Hello-Akka/user/Fat-Panda/$a
      //            akka://Hello-Akka/user/Fat-Panda/$b
      println(s" I am ${self.path.name}")
    case _ => println(s" I do not understand the message")
  }
}