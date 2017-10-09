package akka_cookbook.chapter6_persistence.sample_persistence_app

import akka.actor.{ActorSystem, Props}

object SamplePersistenceApp extends App {
  val system = ActorSystem("example")
  val persistentActor1 = system.actorOf(Props[SamplePersistenceActor])
  val persistentActor2 = system.actorOf(Props[SamplePersistenceActor])

  persistentActor1 ! UserUpdate("foo", Add)
  persistentActor1 ! UserUpdate("baz", Add)
  persistentActor1 ! "snap"
  persistentActor1 ! "print"
  persistentActor1 ! UserUpdate("baz", Add)
  persistentActor1 ! "print"

  Thread.sleep(2000)
  system.stop(persistentActor1)

  persistentActor2 ! "print"
  Thread.sleep(2000)

  system.terminate()
}
