package common

import akka_cookbook.chapter7_remoting_and_clustering.sec9_cluster_sharding.ClusterShardingApplication1.res

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

/**
  * Display all actors states
  */
class PrivateMethodCaller(x: AnyRef, methodName: String) {
  def apply(_args: Any*): Any = {
    val args = _args.map(_.asInstanceOf[AnyRef])

    def _parents: Stream[Class[_]] = Stream(x.getClass) #::: _parents.map(_.getSuperclass)

    val parents = _parents.takeWhile(_ != null).toList
    val methods = parents.flatMap(_.getDeclaredMethods)
    val method = methods.find(_.getName == methodName).getOrElse(throw new IllegalArgumentException("Method " + methodName + " not found"))
    method.setAccessible(true)
    method.invoke(x, args: _*)
  }

}

class PrivateMethodExposer(x: AnyRef) {
  def apply(method: scala.Symbol): PrivateMethodCaller = new PrivateMethodCaller(x, method.name)
}