package future

import scala.concurrent._
import ExecutionContext.Implicits.global
/**
  * Created by xiangnanren on 02/10/2017.
  */
object MethosInFuture extends App {
  val f1 = Future { 4/1 }
  val f2 = Future { 2/0 } recover { case e: Exception => 3 }
  val f3 = Future { 2/0 }

  val parts = Array(f1, f1)
  val futures = parts.map(p => Future { p.value.get.get })

  val result = Future.firstCompletedOf(futures)
}
