package akka_cookbook.chapter8_stream.streamingIO
import java.net._
import java.io._
import scala.io._

/**
  * @author Xiangnan Ren
  */
object SocketPublisher extends App {
  val s = new Socket(InetAddress.getByName("127.0.0.1"), 1234)
  lazy val in = new BufferedSource(s.getInputStream).getLines()
  val out = new PrintStream(s.getOutputStream)

  out.print("Hello, world")
  out.flush()
  println("Received: " + in.next())

  s.close()
}
