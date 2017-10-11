package future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
/**
  * Created by xiangnanren on 02/10/2017.
  */
object WaitForResults {

  def main(args: Array[String]): Unit = {
    val f = Future { Thread.sleep(2000); 42 }

    // If the task throws an exception, it is rethrown in the call to Await
    Await.ready(f, 1.nanosecond)

    // blocks for 2 seconds and then yields the result of the future.
    val result = Await.result(f, 2.seconds)
    val Some(t) = f.value

    println(t + " " + result )

  }
}
