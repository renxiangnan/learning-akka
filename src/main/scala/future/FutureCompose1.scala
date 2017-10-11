package future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by xiangnanren on 02/10/2017.
  */
object FutureCompose1 extends App {
  val f1 = Future { 4/1 }
  val f2 = Future { 2/0 } recover { case e: Exception => 3 }
  val f3 = Future { 2/0 }

  val combined: Future[Int] = f1.map(n1 => n1 + 2/2)
  val combined1: Future[Future[Int]] = f1.map(n1 => f2.map(n2 => n1 + n2))
  val combined2 = f1.flatMap(n1 => f2.map(n2 => n1 + n2))
  val combined3 = for (n1 <- f1; n2 <- f2) yield n1 + n2

  combined3.onComplete {
    case Success(r) => println(s"Result = $r")
    case Failure(ex) => println(ex.getMessage)
  }

  combined3.foreach(n => println(s"Result: $n"))

  Await.result(combined3, 1.seconds)




}
