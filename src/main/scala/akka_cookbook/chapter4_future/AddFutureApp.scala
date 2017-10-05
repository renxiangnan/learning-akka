package akka_cookbook.chapter4_future

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by xiangnanren on 05/10/2017.
  */
object AddFutureApp extends App {
  val future = Future(1+2).mapTo[Int]
  val sum = Await.result(future, 10.seconds)
  println(s"Future result $sum")

}
