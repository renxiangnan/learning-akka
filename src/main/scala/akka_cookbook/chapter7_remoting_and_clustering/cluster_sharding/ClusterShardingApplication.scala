package akka_cookbook.chapter7_remoting_and_clustering.cluster_sharding
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.pattern.ask
import akka.util.Timeout
import akka_cookbook.chapter7_remoting_and_clustering.cluster_sharding.TemperatureActor.{GetCurrentTemperature, Location, UpdateTemperature}

import scala.concurrent.duration._
/**
  * @author Xiangnan Ren
  */

/**
  * This recipe test the feature of cluster sharding in the following scenario:
  * we are receiving current temperature data from a city. At some point, we decide
  * to scale the app globally, but the data won't fit on a single machine. So we scale
  * up using Cluster Sharding
  */
object ClusterShardingApplication extends App {
  val actorSystem = ActorSystem("ClusterSystem")
  import actorSystem.dispatcher

  val temperatureActor: ActorRef = ClusterSharding(actorSystem).
    start(
      typeName = TemperatureActor.shardName,              //This defines the entity name
      entityProps = Props[TemperatureActor],              // Refers to the prop object of the actor that we want to create
      settings = ClusterShardingSettings(actorSystem),    // Sharding settings
      extractEntityId = TemperatureActor.extractEntityId, // Application-specific function that defines an entity (Country-City in this use case)
      extractShardId = TemperatureActor.extractShardId)   // Application-specific function that defines a shard

  // Simulate time passed. Never use Thread.sleep in production codes.
  Thread.sleep(2000)

  val locations = Vector(
    Location("USA", "Chicago"),
    Location("ESP", "Madrid"),
    Location("FIN", "Helsinki"))

  temperatureActor ! UpdateTemperature(locations(0), 1.0)
  temperatureActor ! UpdateTemperature(locations(1), 20.0)
  temperatureActor ! UpdateTemperature(locations(2), -10.0)

  implicit val timeout: Timeout = Timeout(5.seconds)
  locations.foreach { location =>
      (temperatureActor ? GetCurrentTemperature(location)).
        onSuccess {
          case x: Double =>
            println(s"Current temperature in $location is $x")
        }
  }
}
