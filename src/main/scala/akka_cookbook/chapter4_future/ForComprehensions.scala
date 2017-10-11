package akka_cookbook.chapter4_future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by xiangnanren on 05/10/2017.
  */
object ForComprehensions extends App {
  val futureA = Future(20 + 20)
  val futureB = Future(30 + 30)
  val finalFuture: Future[Int] =
    for{a <- futureA
        b <- futureB} yield a + b

  println("Future result is " + Await.result(finalFuture, 1.seconds))
}
