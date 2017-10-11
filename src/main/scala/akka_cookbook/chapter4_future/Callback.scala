package akka_cookbook.chapter4_future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
/**
  * Created by xiangnanren on 05/10/2017.
  */
object Callback extends App {
  val future = Future(1 + 2).mapTo[Int]
  future onComplete{
    case Success(result) => println(s"result is $result")
    case Failure(fail) => fail.printStackTrace()
  }
  println("Executed before callback")

}
