package akka_cookbook.chapter8_stream.error_handling

import akka.actor.ActorSystem
import akka.stream.{ActorAttributes, ActorMaterializer,
ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl._


/**
  * @author Xiangnan Ren
  */

object HandlingErrorApp extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("HandlingErrors")

  val streamDecider: Supervision.Decider = {
    case e: IndexOutOfBoundsException =>
      println("Dropping element because of IndexOutOfBoundException. Resuming.")
      Supervision.Resume

    case _ => Supervision.Stop
  }

  val flowDecider: Supervision.Decider = {
    case e: IllegalArgumentException =>
      println("Dropping element because of IllegalArgumentException. Restarting.")
      Supervision.Restart

    case _ => Supervision.Stop
  }

  val actorMaterializerSettings = ActorMaterializerSettings(actorSystem).
    withSupervisionStrategy(streamDecider)

  implicit val actorMaterializer =
    ActorMaterializer(actorMaterializerSettings)

  val words = List("Handling", "Error", "In", "Akka", "Streams", "")

  val flow = Flow[String].map(word => {
   if(word.length == 0)
     throw new IllegalArgumentException("Empty words are not allowed")
   word
  }).withAttributes(ActorAttributes.supervisionStrategy(flowDecider))

  Source(words).via(flow).map(array => array(2)).to(Sink.foreach(println)).run()


}
