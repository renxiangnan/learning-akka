package future
import java.time._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
  * Created by xiangnanren on 02/10/2017.
  */
object HelloFuture {
  def main(args: Array[String]): Unit = {
    Future {
      Thread.sleep(1000)
      println(s"This is the future at ${LocalTime.now}")
    }
    println(s"This is the present at ${LocalTime.now}")


    // Multiple futures execute concurrently
    Future { for (i <- 1 to 100) { print("A");  } }
    Future { for (i <- 1 to 100) { print("B");  } }

    val f2 = Future {
      if (LocalTime.now.getHour > 12)
        throw new Exception("too late")
      42
    }



    Thread.sleep(10000)
  }
}
