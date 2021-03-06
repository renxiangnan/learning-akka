name := "learning-akka"

version := "1.0"

scalaVersion := "2.11.7"
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }


val akkaVersion = "2.4.17"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "3.0.1",
  "com.typesafe" % "config" % "1.3.1",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" % "akka-agent_2.11" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "com.typesafe.akka" % "akka-typed-experimental_2.11" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" % "akka-persistence_2.11" % akkaVersion,
  "com.typesafe.akka" % "akka-remote_2.11" % akkaVersion,
  "com.typesafe.akka" % "akka-cluster-tools_2.11" % akkaVersion,
  "com.typesafe.akka" % "akka-cluster-sharding_2.11" % akkaVersion,
  "com.typesafe.akka" % "akka-distributed-data-experimental_2.11" % "2.4.17",
  "com.typesafe.akka" % "akka-cluster-tools_2.11" % akkaVersion,
  "com.typesafe.akka" % "akka-stream_2.11" % akkaVersion,
  "com.typesafe.akka" % "akka-stream-kafka_2.11" % "0.13",
  "com.typesafe.akka" %%"akka-stream-testkit"  % akkaVersion,
  "com.typesafe.akka" % "akka-http_2.11" % "10.0.5",
  "org.iq80.leveldb" % "leveldb" % "0.7"
)
    