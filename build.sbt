name := "murmur"

version := "0.1"

scalaVersion := "2.11.8"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq (
    "com.typesafe.akka" %% "akka-actor" % "2.4.9",
    "com.typesafe.akka" %% "akka-http-experimental" % "2.4.9",
    "com.typesafe.akka" %% "akka-http-core" % "2.4.9"
)
