package akka_cookbook.chapter6_persistence.FSM

import akka.actor.{ActorLogging, Props}
import akka.persistence.fsm.PersistentFSM
import scala.reflect.ClassTag

class PersistentFSMActor(_persistenceId: String)
                        (implicit val domainEventClassTag: ClassTag[DomainEvent])
  extends PersistentFSM[CountDownLatchState, Count, DomainEvent] with ActorLogging {

  startWith(Closed, Count())
  when(Closed) {
    case Event(Initialize(count), _) =>
      log.info(s"Initializing countdown latch with count $count")
      stay applying LatchDownClosed(count)

    case Event(Mark, Count(n)) if n != 0 =>
      log.info(s"Still $n to open gate.")
      stay applying LatchDownClosed(n)

    case Event(Mark, _) =>
      log.info(s"Gate open.")
      goto(Open) applying LatchDownOpen
  }
  when(Open) {
    case Event(Initialize(count), _) =>
      goto(Closed) applying LatchDownClosed(count)
  }


  override def applyEvent(domainEvent: DomainEvent,
                          countdown: Count): Count =
    domainEvent match {
      case LatchDownClosed(i) => Count(i-1)
      case LatchDownOpen => Count()
  }

  override def preStart(): Unit = log.info("Starting.")
  override def postStop(): Unit = log.info("Stopping.")
  override def persistenceId: String = _persistenceId

}

object PersistentFSMActor {
  def props(persistenceId: String) =
    Props(new PersistentFSMActor(persistenceId))
}
