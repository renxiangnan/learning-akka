package akka_cookbook.chapter4_future

import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by xiangnanren on 05/10/2017.
  */
object ReducingFutures extends App {
  val timeout = Timeout(10.seconds)
  val listOfFutures = (1 to 100).map(Future(_))
  val finalFuture = Future.reduce(listOfFutures)(_ + _)
  println(s"sum of numbers from 1 to 10 is ${Await.result(finalFuture, 10.seconds)}")

}
