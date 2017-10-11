package akka_cookbook.chapter7_remoting_and_clustering.clustering

import akka.actor.Actor

/**
  * @author Xiangnan Ren
  */

class WorkerActor extends Actor {
  override def receive: Receive = {
    case Work(workId) =>
      Thread.sleep(3000)
      sender ! WorkDone(workId)
      println(s"Work $workId wad done by worker actor")
  }

}

case class Work(workId: String)
case class WorkDone(workId: String)


