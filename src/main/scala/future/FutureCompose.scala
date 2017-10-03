package future


import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}
/**
  * Created by xiangnanren on 02/10/2017.
  */
object FutureCompose extends App {
  val f1 = Future { 4/1 }
  val f2 = Future { 2/2 }

  f1.onComplete{
    case Success(r1) =>
      f2.onComplete{
        case Success(r2) => println(s"r1 + r2 = ${r1+r2}")
        case Failure(ex) => println("Task 2 fails " + ex.getMessage)
      }
    case Failure(ex) => println("Task 1 fails " + ex.getMessage)
  }

  Await.result(f1, 1.seconds)
  Await.result(f2, 1.seconds)


}
