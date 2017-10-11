package future


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by xiangnanren on 02/10/2017.
  */
object TryClass extends App {
  val t = Future { 1/2}
  val t1 = Future { 1/0}

 Await.result(t, 1.seconds)

  t.value.get match {
    case Success(v) => println(s"Task success! v = $v")
    case Failure(v) => println(v.getMessage)
  }

  t1.value.get match {
    case Success(v) => println(s"Task successes! v = $v")
    case Failure(v) => println("Task fails " + v.getMessage)
  }

  if (t.value.get.isSuccess) println("Task success again")
  else println("Task fails")

  if (t1.value.get.isSuccess) println("Task success again")
  else println("Task fails")

}
