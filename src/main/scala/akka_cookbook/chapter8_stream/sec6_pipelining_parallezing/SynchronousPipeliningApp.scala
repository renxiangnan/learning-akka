package akka_cookbook.chapter8_stream.sec6_pipelining_parallezing

import akka.stream.scaladsl._


/**
  * @author Xiangnan Ren
  */
object SynchronousPipeliningApp extends PipeliningParallelizing {
  runGraph(Flow[Wash].via(washStage).via(dryStage))
}

object AsynchronousPipeliningApp extends PipeliningParallelizing {
  runGraph(Flow[Wash].via(washStage.async).via(dryStage.async))
}

object ParallelizingApplication extends PipeliningParallelizing {
  runGraph(Flow[Wash].via(parallelStage))
}
