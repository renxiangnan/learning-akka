package akka_cookbook.chapter7_remoting_and_clustering.scaling_out

import akka.actor.{Actor, ActorRef, Terminated}

import scala.util.Random

/**
  * @author Xiangnan Ren
  */

class MasterActor extends Actor {
  var workers = List.empty[ActorRef]

  override def receive: Receive = {
    case RegisterWorker(workerActor) =>
      context.watch(workerActor)
      workers = workerActor :: workers

    case Terminated(actorRef) =>
      println(s"Actor ${actorRef.path.address} has been terminated. " +
        s"Removing from available workers.")
      workers = workers.filterNot(_==actorRef)

    case _: Work if workers.isEmpty =>
      println("We cannot process your work since thers is no workers.")

    case work: Work =>
      workers(Random.nextInt(workers.size)) ! work

    case WorkDone(workId) =>
      println(s"Work with id $workId is done.")
  }
}

case class RegisterWorker(workerActor: ActorRef)

