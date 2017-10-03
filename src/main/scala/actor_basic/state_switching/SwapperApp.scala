package actor_basic.state_switching

import akka.actor._
import akka.event.Logging

/**
  * Created by xiangnanren on 01/10/2017.
  */
object SwapperApp extends App {
  val system = ActorSystem("SwapperSystem")

  val swap = system.actorOf(Props[Swapper], name = "swapper")
  swap ! Swap // logs Hi
  swap ! Swap // logs Ho
  swap ! Swap // logs Hi
  swap ! Swap // logs Ho
  swap ! Swap // logs Hi
  swap ! Swap // logs Ho
}

case object Swap

class Swapper extends Actor {
  import context._
  val log = Logging(system, this)

  def receive = {
    case Swap => log.info("Hi")
      become({ case Swap => log.info("Ho")
        // resets the latest 'become' (just for fun)
        unbecome() }, discardOld = false) // push on top instead of replace
  }
}
