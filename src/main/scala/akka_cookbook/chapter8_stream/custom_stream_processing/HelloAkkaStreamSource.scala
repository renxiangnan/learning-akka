package akka_cookbook.chapter8_stream.custom_stream_processing

import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}

/**
  * @author Xiangnan Ren
  */


/**
  * Custom Source Stage
  */
class HelloAkkaStreamSource extends GraphStage[SourceShape[String]]{
  val out: Outlet[String] = Outlet("SystemInputSource")

  override def shape = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes):
  GraphStageLogic = new GraphStageLogic(shape) {
    setHandler(out, new OutHandler {
      // Called by downstream stage where is a demand for new element
      override def onPull(): Unit = {
        val line = "Hello World Akka Streams!"
        push(out, line)
      }
    })
  }
}

