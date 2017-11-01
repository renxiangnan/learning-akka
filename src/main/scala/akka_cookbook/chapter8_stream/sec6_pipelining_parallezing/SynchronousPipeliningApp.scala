package akka_cookbook.chapter8_stream.sec6_pipelining_parallezing

import akka.stream.scaladsl._


/**
  * @author Xiangnan Ren
  */
object SynchronousPipeliningApp extends App with PipeliningParallelizing {
  runGraph(Flow[Wash].via(washStage).via(dryStage))
}

object AsynchronousPipeliningApp extends App with PipeliningParallelizing {
  runGraph(Flow[Wash].via(washStage.async).via(dryStage.async))
}

object ParallelizingApplication extends App with PipeliningParallelizing {
  runGraph(Flow[Wash].via(parallelStage))
}
