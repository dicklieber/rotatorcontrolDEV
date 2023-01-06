import sbtassembly.MergeStrategy

import java.nio.file.{Files, Paths}
import scala.language.postfixOps

ThisBuild / version := {
  Files.readString(Paths.get("version.txt"))
}


ThisBuild / scalaVersion := "2.13.10"

logBuffered := false

lazy val output = {
  val jarsPath = Paths.get("target/jars").toAbsolutePath
  if (Files.notExists(jarsPath)) {
    val created = Files.createDirectories(jarsPath)
    println(s"created: ${created.toFile}")
  }
  jarsPath.resolve("rotatorcontrol.jar")
}


lazy val rotatorcontrol = (project in file("."))
  .settings(
    assembly / assemblyJarName := "rotatorcontrol.jar",
    // more settings here ...
  )

fork := false

enablePlugins(JvmPlugin, GitPlugin, BuildInfoPlugin)
buildInfoKeys ++= Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion,
  git.gitCurrentTags, git.gitCurrentBranch, git.gitHeadCommit, git.gitHeadCommitDate, git.baseVersion)
buildInfoPackage := "com.wa9nnn.rotator"

buildInfoOptions ++= Seq(
  BuildInfoOption.ToJson,
  BuildInfoOption.BuildTime
)

resolvers += ("Reposilite" at "http://194.113.64.105:8080/releases")
  .withAllowInsecureProtocol(true)

val logbackVersion = "1.2.3"

libraryDependencies ++= Seq(
  "com.wa9nnn" %% "util" % "0.1.9",
  "org.scalafx" %% "scalafx" % "19.0.0-R30" excludeAll (
    ExclusionRule(organization = "org", "openjfx"),
    ),
  "org.scalafx" %% "scalafx-extras" % "0.7.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",

  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "ch.qos.logback" % "logback-core" % logbackVersion,

  "org.specs2" %% "specs2-core" % "4.6.0" % "test",
  "org.specs2" %% "specs2-mock" % "4.6.0" % "test",
  "com.github.scopt" %% "scopt" % "4.0.1",

  "org.jfree" % "jfreechart" % "1.5.3",
  "org.jfree" % "jfreechart-fx" % "1.0.1",
  "com.typesafe.play" %% "play-json" % "2.9.3",
  "com.google.inject" % "guice" % "5.1.0",
  "net.codingwell" %% "scala-guice" % "5.1.0",
  "nl.grons" %% "metrics4-scala" % "4.2.9",
  "io.dropwizard.metrics" % "metrics-jmx" % "4.2.13",
  "com.typesafe" % "config" % "1.4.2",
)

resolvers +=
  "ReposiliteXYZZY" at "http://127.0.0.1:8080/releases"

ThisBuild / assemblyMergeStrategy := {
  case p if p.startsWith("javafx") =>
    MergeStrategy.discard
  case PathList("META-INF", xs@_*) =>
    MergeStrategy.discard
  case _ =>
    MergeStrategy.first
}