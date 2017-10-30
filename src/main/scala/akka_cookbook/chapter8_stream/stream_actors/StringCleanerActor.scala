package akka_cookbook.chapter8_stream.stream_actors

import akka.actor.Actor

/**
  * @author Xiangnan Ren
  */
class StringCleanerActor extends Actor {
  override def receive = {
    case s: String =>
      println(s"Cleaning [$s] in StringCleaner")
      sender() ! s.replaceAll("\\p{Punct}", "").
        replaceAll(System.lineSeparator(), "")
  }
}
