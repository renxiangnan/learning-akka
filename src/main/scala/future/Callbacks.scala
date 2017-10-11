package future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Failure, Success}


/**
  * Created by xiangnanren on 02/10/2017.
  */
object Callbacks extends App {

  val f = Future {
    Thread.sleep(100)
    if (Math.random() < 0.5) throw new Exception
    42
  }

  f.onComplete {
    case Success(v) => println(s"The answer is $v")
    case Failure(ex) => println(ex.getMessage)
  }

  Await.result(f, 1.seconds)
  Thread.sleep(2000)


}
