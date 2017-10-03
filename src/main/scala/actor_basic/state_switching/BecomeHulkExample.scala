package actor_basic.state_switching

import akka.actor._

/**
  * Created by xiangnanren on 01/10/2017.
  */

/**
  * Use the Akka “become” approach.
  * To do this, first define the different possible states the actor can be in.
  * Then, in the actor’s receive method, switch between the different states
  * based on the messages it receives.
  */

object BecomeHulkExample extends App {
  val system = ActorSystem("BecomeHulkExample")
  val davidBanner = system.actorOf(Props[DavidBanner], name = "DavidBanner")

  // The davidBanner instance is
  // sent the ActNormalMessage to
  // set an initial state.
  davidBanner ! ActNormalMessage

  // After sending davidBanner a TryToFindSolution message,
  // it sends a BadGuysMakeMeAngry message.
  davidBanner ! TryToFindSolution
  davidBanner ! BadGuysMakeMeAngry
  Thread.sleep(1000)
  davidBanner ! ActNormalMessage
  system.terminate()
}

import akka.actor._
case object ActNormalMessage
case object TryToFindSolution
case object BadGuysMakeMeAngry

class DavidBanner extends Actor {
  import context._
  def angryState: Receive = {
    case ActNormalMessage =>
      println("Phew, I'm back to being David.")
      become(normalState)
  }

  def normalState: Receive = {
    case TryToFindSolution =>
    println("Trying to find a solution for my problem ...")

    case BadGuysMakeMeAngry =>
    println("I'm getting angry...")
    become(angryState)

  }

  def receive = {
    case ActNormalMessage => become(normalState)
    case BadGuysMakeMeAngry => become{
      angryState
    }
  }
}