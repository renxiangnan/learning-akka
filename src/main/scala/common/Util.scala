package common

/**
  * Created by xiangnanren on 05/10/2017.
  */
object Util {
  def displayTime[T](t: => T): Unit = {
    val tStart = System.nanoTime()
    t
    println((System.nanoTime() - tStart)/1e6)
  }

}
