
import akka_cookbook.chapter8_stream.examples_akka_io.modularity.IntervalBasedThrottlerSettings
import org.scalatest.{FlatSpec, Matchers}
import akka_cookbook.chapter8_stream.examples_akka_io.modularity.IntervalBasedThrottlerSettings._

import scala.concurrent.duration._

/**
  * @author Xiangnan Ren
  */

class IntervalBasedThrottlerSettingsSpec extends FlatSpec with Matchers {

  it should "return correct minimum interval between events" in {
    assertInterval(2.perSecond, 500.millis)
    assertInterval(20.perSecond, 50.millis)
    assertInterval(200.per(1.second), 5.millis)
    assertInterval(120.per(60.seconds), 500.millis)
    assertInterval(120.per(1.minute), 500.millis)
    assertInterval(120.per(1.minute), 500.millis)
    assertInterval(1000.perSecond, 1.millis)
    assertInterval(1000.perSecond, 1.millis)
  }

  private def assertInterval(ts: IntervalBasedThrottlerSettings, expectedInterval: FiniteDuration): Unit = {
    ts.interval.compare(expectedInterval) shouldBe 0
  }

}