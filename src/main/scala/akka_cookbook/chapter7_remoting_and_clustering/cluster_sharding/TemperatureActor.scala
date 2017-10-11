package akka_cookbook.chapter7_remoting_and_clustering.cluster_sharding
import akka.actor.Actor
import akka.cluster.Cluster
import akka.cluster.sharding.ShardRegion

/**
  * @author Xiangnan Ren
  */

class TemperatureActor extends Actor {
  import TemperatureActor._
  var temperatureMap = Map.empty[Location, Double]

  override def preStart(): Unit = {
    println(s"I have been created at " +
      s"${Cluster(context.system).selfUniqueAddress}")
  }

  /*
    @ binds the object being pattern matched to a variable.
    msg @ GetCharLog will result in msg holding a reference to the GetCharLog object,
    which isn't very useful. A better example is msg @ Foo(a, b, c),
    which will result in msg holding a reference to the instance of Foo that is matched on,
    which lets you forward the received message (for example)
    without needing to construct another instance of Foo with a, b, and c.
   */
  override def receive: Receive = {
    case _ @ UpdateTemperature(location, currentTemp) =>
      temperatureMap += (location -> currentTemp)
      println(s"Temp update: $location")

    case GetCurrentTemperature(location) =>
      sender ! temperatureMap(location)
  }
}

object TemperatureActor {

  case class Location(country: String, city: String) {
    override def toString: String = s"$country-$city"
  }
  case class UpdateTemperature(location: Location, currentTemp: Double)
  case class GetCurrentTemperature(location: Location)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case msg @ UpdateTemperature(location, _) => (s"$location", msg)
    case msg @ GetCurrentTemperature(location) => (s"$location", msg)
  }

  val numberOfShards = 100
  // ExtractShardId: Msg => ShardId (String)
  val extractShardId: ShardRegion.ExtractShardId = {
    case UpdateTemperature(location, _) =>
      // partitioning by finding remainder
      (s"$location".hashCode % numberOfShards).toString
    case GetCurrentTemperature(location) =>
      (s"$location".hashCode % numberOfShards).toString
  }

  val shardName = "Temperature"
}
