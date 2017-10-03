name := "learning-akka"

version := "1.0"

scalaVersion := "2.11.7"

lazy val akkaVersion = "2.5.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "com.typesafe.akka" %% "akka-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion

)
    