package akka_cookbook.chapter8_stream.examples_akka_io

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.FanInShape.{Init, Name}
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge, MergePreferred, RunnableGraph, Sink, Source}
import akka.stream._

import scala.collection.immutable

/**
  * @author Xiangnan Ren
  */


/**
  *
  *     Source_1_[N] --->
  *                      | ---> priorityPool1 --->
  *     Source_1_[P] --->       (workerType1)     | ---> priorityPool2 --->
  *                                               |      (workerType2)
  *                              Source_2_[N] --->
  *
  */
object App6_PriorityWorkerPool extends App {
  implicit val system = ActorSystem("sys")
  implicit val materializer = ActorMaterializer()

  // Define two worker flows
  val workerType1 = Flow[String].map("Processed By worker 1 " + _)
  val workerType2 = Flow[String].map("Processed By worker 2 " + _)

  RunnableGraph.fromGraph(GraphDSL.create(){ implicit builder =>
    import GraphDSL.Implicits._

    // Create two priority pools (graph)
    val priorityPool1 = builder.add(PriorityWorkerPool(workerType1, 4))
    val priorityPool2 = builder.add(PriorityWorkerPool(workerType2, 2))

    Source(1 to 50).map("[N] job: " + _) ~> priorityPool1.jobsIn
    Source(1 to 50).map("[P] First, Priority job: " + _) ~> priorityPool1.priorityJobsIn

    priorityPool1.resultOut ~> priorityPool2.jobsIn
    Source(1 to 50).map("[P] Second, priority job: " + _) ~> priorityPool2.priorityJobsIn

    priorityPool2.resultOut ~> Sink.foreach(println)

    ClosedShape
  }).run()

  Thread.sleep(1000)

}


/**
  * Use defined junction PriorityWorkerPoolShape to wire up the graph to
  * present the worker pool.
  *
  *      1. Merge incoming normal and priority jobs using MergedPreferred.
  *      2. Send jobs to balance junction, which fan-outs to a configurable number of workers (flows).
  *      3. Merge all results together and send them out through the only output port.
  *
  *        --- Normal Jobs --->                                   ---> Worker 1 --->
  *                            | MergedPreferred ---> BalancePool |                 | ---> Merge
  *      --- Priority Jobs --->  (priorityMerge)                  ---> Worker 2 --->   (resultsMerge)
  *
  */
object PriorityWorkerPool {
  def apply[In, Out](worker: Flow[In, Out, Any],
                     workerCount: Int): Graph[PriorityWorkerPoolShape[In, Out], NotUsed] = {

    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      /*  Merge several streams, taking elements as they arrive from input streams.
          (picking from preferred when several have elements ready).

          A `MergePreferred` has one `out` port, one `preferred` input port and 0 or more secondary `in` ports.
          Here, we define a single secondary input port
      */
      val priorityMerge = builder.add(MergePreferred[In](1))
      val balance = builder.add(Balance[In](workerCount))
      val resultsMerge = builder.add(Merge[Out](workerCount))

      priorityMerge ~> balance

      for(i <- 0 until workerCount)
        balance.out(i) ~> worker ~> resultsMerge.in(i)

      PriorityWorkerPoolShape(
        jobsIn = priorityMerge.in(0),
        priorityJobsIn = priorityMerge.preferred,
        resultOut = resultsMerge.out)
    }
  }
}



/**
  * Create the custom priority worker pool shape (junction) to create its corresponding graph.
  * We will build a graph junction that represents a pool of workers, where a worker is expressed as a Flow[I,O,_]
  *
  *        - jobs type:      I
  *        - result type:    O
  *
  * This reusable worker pool junction will not preserve the order of the incoming jobs.
  * A Balance junction is used to schedule jobs to available worker.
  *
  * The junction has two input ports of type I.
  *
  *
  * In general a custom Shape needs to be able to provide all its input and output ports,
  * be able to copy itself, and also be able to create a new instance from given ports.
  *
  */
case class PriorityWorkerPoolShape[In, Out](jobsIn: Inlet[In],
                                            priorityJobsIn: Inlet[In],
                                            resultOut: Outlet[Out]
                                           ) extends Shape {
  // It is important to provide the list of all input and output
  // ports with a stable order. Duplicates are not allowed.
  override val inlets = jobsIn :: priorityJobsIn :: Nil
  override val outlets = resultOut :: Nil

  override def deepCopy() = PriorityWorkerPoolShape(
    jobsIn.carbonCopy(),
    priorityJobsIn.carbonCopy(),
    resultOut.carbonCopy()
  )

  override def copyFromPorts(inlets: immutable.Seq[Inlet[_]],
                             outlets: immutable.Seq[Outlet[_]]) = {
    assert(inlets.size == this.inlets.size)
    assert(outlets.size == this.outlets.size)

    // This is why order matters when overriding inlets and outlets.
    PriorityWorkerPoolShape[In, Out](
      inlets.head.as[In], inlets(1).as[In], outlets.head.as[Out])

  }
}



// Not used...
class PriorityWorkerPoolShape2[In, Out](_init: Init[Out] = Name("PriorityWorkerPool"))
  extends FanInShape[Out](_init) {
  override protected def construct(init: Init[Out]) = new PriorityWorkerPoolShape2(init)

  val jobsIn = newInlet[In]("jobsIn")
  val priorityJobsIn = newInlet[In]("priorityJobsIn")
}

