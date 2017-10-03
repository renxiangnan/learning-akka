package future

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}
/**
  * Created by xiangnanren on 03/10/2017.
  */
object PromisesTest {
  def computeAnswer(arg: String) = Future {
    val n = arg + " in computeAnswer"
    n
  }

  // Calling future on a promise yields the associated Future object.
  def computeAnswer1(arg: String) = {
    val p = Promise[Int]()
    Future {
      val n = 10
      p.success(n)
      20
    }
    p.future
  }
}
