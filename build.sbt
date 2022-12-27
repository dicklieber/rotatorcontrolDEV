import sbt.Keys.streams
import sbtrelease.ReleasePlugin.autoImport.releaseStepTask

import java.nio.file.{Files, Paths}
import scala.language.postfixOps
import scala.sys.process._

ThisBuild / scalaVersion := "2.13.10"

//lazy val root = (project in file("."))
//  .settings(
//    name := "rotatorcontrol"
//  )

fork := false


maintainer := "Dick Lieber <wa9nnn@u505.com>"
packageSummary := "ARCO to HamLibs rotctld"
packageDescription := """Adapts ARCO Rotator Controllers to rotctld protocol"""

enablePlugins(JavaAppPackaging, GitPlugin, BuildInfoPlugin, UniversalPlugin, UniversalDeployPlugin)
buildInfoKeys ++= Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, maintainer,
  git.gitCurrentTags, git.gitCurrentBranch, git.gitHeadCommit, git.gitHeadCommitDate, git.baseVersion)
buildInfoPackage := "com.wa9nnn.rotator"

buildInfoOptions ++= Seq(
  BuildInfoOption.ToJson,
  BuildInfoOption.BuildTime
)

//deploymentSettings

jlinkIgnoreMissingDependency := JlinkIgnore.everything

resolvers += ("Reposilite" at "http://194.113.64.105:8080/releases")
  .withAllowInsecureProtocol(true)


val logbackVersion = "1.2.3"


libraryDependencies ++= Seq(
  "com.wa9nnn" %% "util" % "0.1.9",
  "org.scalafx" %% "scalafx" % "19.0.0-R30",
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

publish / skip := true


import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

bashScriptExtraDefines += "umask 077"

val ghRelease = taskKey[Unit]("send stuff to github")

ghRelease := {
  val log = streams.value.log
  try {
    log.info("=========ghRelease=========")
    lazy val osName = System.getProperty("os.name") match {
      case n if n.startsWith("Linux") => "linux"
      case n if n.startsWith("Mac") => "mac"
      case n if n.startsWith("Windows") => "win"
      case _ => throw new Exception("Unknown platform!")
    }
    log.info(s"osName: $osName")

    val relVersion = s"v${version.value}-$osName"
    log.info(s"relVersion: $relVersion")

    val pubArtifact: File =  osName match {
      case "mac" =>
        log.info("Using: packageOsxDmg")
        (Universal / packageOsxDmg).value
      case x =>
        log.info("Using: packageBin")
        (Universal / packageBin).value
    }
    log.info(s"pubArtifact: $pubArtifact")

    val github: java.nio.file.Path = Paths.get("github.sh")
    log.info(s"github path: $github Executable: ${Files.isExecutable(github)}")

    val abs: File = github.toAbsolutePath.toFile
    log.info(s"github abs: $abs")

    log.info(s"relVersion: $relVersion")
    log.info(s"pubArtifact: $pubArtifact")

    val cmd = s"""gh release create --generate-notes $relVersion $pubArtifact"""
    log.info((s"cmd: $cmd"))
    Process(cmd) ! log
    log.info(s"\tcmd: $cmd done")
  } catch {
    case e:Exception =>
      e.printStackTrace()
  }

}

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // : ReleaseStep
  inquireVersions, // : ReleaseStep
  runClean, // : ReleaseStep
  runTest, // : ReleaseStep
  setReleaseVersion, // : ReleaseStep
  commitReleaseVersion, // : ReleaseStep, performs the initial git checks
//  tagRelease, // : ReleaseStep
  releaseStepTask(ghRelease),
  //  releaseStepTask(Universal / packageBin),
  //  publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
//  pushChanges, // : ReleaseStep, also checks that an upstream branch is properly configured
  setNextVersion, // : ReleaseStep
  commitNextVersion, // : ReleaseStep
  pushChanges, // : ReleaseStep, also checks that an upstream branch is properly configured
)


//Universal / javaOptions ++= Seq(
//  "-java-home ${app_home}/../jre"
//)





resolvers +=
  "ReposiliteXYZZY" at "http://127.0.0.1:8080/releases"

