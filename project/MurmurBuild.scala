import sbt.Keys._
import sbt._

object MurmurBuild extends Build {

    lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
        organization := "com.liangdrew",
        version := "0.1",
        scalaVersion := "2.11.8"
    )

    // Versions
    val AKKA_VERSION = "2.4.9"
    val UPICKLE_VERSION = "0.4.1"

    // External libraries
    val akkaActor = "com.typesafe.akka" %% "akka-actor" % AKKA_VERSION
    val akkaHttpExperimental = "com.typesafe.akka" %% "akka-http-experimental" % AKKA_VERSION
    val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % AKKA_VERSION
    val upickle = "com.lihaoyi" %% "upickle" % UPICKLE_VERSION

    // Project dependencies
    val commonDependencies = Seq(
        akkaActor,
        akkaHttpExperimental,
        akkaHttpCore,
        upickle
    )

    lazy val root = Project(
        id = "root",
        base = file(".")
    ).aggregate(`chat-engine`, `cl-client`)

    lazy val `chat-engine` = Project(id = "chat-engine", base = file("chat-engine"))
        .settings(commonSettings: _*)
        .settings(libraryDependencies ++= commonDependencies)

    lazy val `cl-client` = Project(id = "cl-client", base = file("cl-client"))
        .dependsOn(`chat-engine`)
        .settings(commonSettings: _*)
        .settings(libraryDependencies ++= commonDependencies)
        .settings(connectInput in run := true)
}