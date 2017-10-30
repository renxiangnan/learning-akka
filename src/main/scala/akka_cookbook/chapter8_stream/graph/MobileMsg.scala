package akka_cookbook.chapter8_stream.graph

import scala.util.Random

/**
  * @author Xiangnan Ren
  */
trait MobileMsg {
  def id = Random.nextInt(1000)
  def toGenMsg(origin: String) = GenericMsg(id, origin)
}

class AndroidMsg extends MobileMsg
class IosMsg extends MobileMsg
case class GenericMsg(id: Int, origin: String)
