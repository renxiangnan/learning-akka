package akka_cookbook.chapter7_remoting_and_clustering.sec5_scaling_out

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

/**
  * @author Xiangnan Ren
  */

// use     -Dconfig.resource=application-2.conf
object ScalingOutWorkerApp extends App {
  val config = ConfigFactory.load("application-2.conf")
  val actorSystem = ActorSystem("WorkerActorSystem", config)
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
  val config = ConfigFactory.load("application-1.conf")

  val actorSystem = ActorSystem("MasterActorSystem", config)
  val masterActor = actorSystem.actorOf(Props[MasterActor], "masterActor")

  (1 to 100).foreach(i => {
    masterActor ! Work(s"$i")
    Thread.sleep(1000)
  })
}

