package akka_cookbook.chapter8_stream.sec6_pipelining_parallezing

import akka.stream.scaladsl.Flow

/**
  * @author Xiangnan Ren
  */
object SynchronousPipeliningApp1 extends App with PipeliningParallelizing1 {
  runGraph(Flow[Clothes].via(washStage).via(dryStage))
}

object AsynchronousPipeliningApp1 extends App with PipeliningParallelizing1 {
  runGraph(Flow[Clothes].via(washStage.async).via(dryStage.async))
}

object ParallelizingApplication1 extends App with PipeliningParallelizing1 {
  runGraph(Flow[Clothes].via(parallelStage))
}