package akka_cookbook.chapter8_stream.custom_stream_processing

import akka.stream.{Attributes, Inlet, SinkShape}
import akka.stream.stage.GraphStage
import akka.stream.stage._
import scala.concurrent.duration._


/**
  * @author Xiangnan Ren
  */

/**
  * Custom Sink Stage
  */
class WordCounterSink extends GraphStage[SinkShape[String]]{
  val in: Inlet[String] = Inlet("WordCounterSink")

  override def shape: SinkShape[String] = SinkShape(in)

  override def createLogic(inheritedAttributes: Attributes):
  GraphStageLogic = new TimerGraphStageLogic(shape) {
    var counts = Map.empty[String, Int].withDefaultValue(0)

    override def preStart(): Unit = {
     schedulePeriodically(None, 100.millisecond)
      // pull requests an element from the stream and manage back pressure.
      pull(in)
    }

    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        // The grab method actually returns the element that comes from the stream
        // It is required to call pull, and then graph to receive an element.
        val word = grab(in)
        counts += word -> (counts(word) + 1)
        pull(in)
      }
    })

    override def onTimer(timerKey: Any): Unit = {
      println(s"At ${System.currentTimeMillis()} count map is $counts")
    }
  }


}
