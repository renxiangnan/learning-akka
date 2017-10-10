package akka_cookbook.chapter7_remoting_and_clustering.clustering

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

// use     -Dconfig.resource=application-2.conf
object ScalingOutWorkerApp extends App {
  val actorSystem = ActorSystem("WorkerActorSystem")
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val selection = actorSystem.actorSelection(
    "akka.tcp://MasterActorSystem@127.0.0.1:2552/user/masterActor")
  selection.resolveOne(3.seconds).onSuccess{
    case masterActor: ActorRef =>
      println("We got the ActorRef for the master actor")
      val pool = RoundRobinPool(10)
      val workerPool =
        actorSystem.actorOf(
          Props[WorkerActor].withRouter(pool), "workerActor")
      masterActor ! RegisterWorker(workerPool)
  }
}

// use    -Dconfig.resource=application-1.conf or
//        -Dconfig.resource=application-3.conf
object ScalingOutMasterApp extends App {
  val actorSystem = ActorSystem("MasterActorSystem")
  val masterActor = actorSystem.actorOf(Props[MasterActor], "masterActor")

  (1 to 100).foreach(i => {
    masterActor ! Work(s"$i")
    Thread.sleep(1000)
  })
}

