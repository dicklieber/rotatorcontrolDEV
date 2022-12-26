import sbt.Keys.streams
import sbtrelease.ReleasePlugin.autoImport.releaseStepTask

import java.nio.file.{Path, Paths}
import scala.language.postfixOps
import scala.sys.process._

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "rotatorcontrol"
  )

fork := false

Universal / mappings := (Universal / mappings).value


maintainer := "Dick Lieber <wa9nnn@u505.com>"
packageSummary := "ARCO to HamLibs rotctld"
packageDescription := """Adapts ARCO Rotator Controllers to rotctld protocol"""

enablePlugins(JavaAppPackaging, GitPlugin, BuildInfoPlugin, UniversalPlugin, UniversalDeployPlugin, WindowsPlugin, JlinkPlugin)
buildInfoKeys ++= Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, maintainer,
  git.gitCurrentTags, git.gitCurrentBranch, git.gitHeadCommit, git.gitHeadCommitDate, git.baseVersion)
buildInfoPackage := "com.wa9nnn.rotator"

buildInfoOptions ++= Seq(
  BuildInfoOption.ToJson,
  BuildInfoOption.BuildTime
)

deploymentSettings

jlinkIgnoreMissingDependency := JlinkIgnore.everything

resolvers += ("Reposilite" at "http://194.113.64.105:8080/releases")
  .withAllowInsecureProtocol(true)


val logbackVersion = "1.2.3"
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux") => "linux"
  case n if n.startsWith("Mac") => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}


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

publish / skip := false

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

bashScriptExtraDefines += "umask 077"

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // : ReleaseStep
  inquireVersions, // : ReleaseStep
  runClean, // : ReleaseStep
  runTest, // : ReleaseStep
  setReleaseVersion, // : ReleaseStep
  commitReleaseVersion, // : ReleaseStep, performs the initial git checks
  tagRelease, // : ReleaseStep
  releaseStepTask(Universal / packageBin),
  //  publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
//  pushChanges, // : ReleaseStep, also checks that an upstream branch is properly configured
  setNextVersion, // : ReleaseStep
  commitNextVersion, // : ReleaseStep
  pushChanges, // : ReleaseStep, also checks that an upstream branch is properly configured
  releaseStepTask(ghRelease),
)


Universal / javaOptions ++= Seq(
  "-java-home ${app_home}/../jre"
)

/*val ghVersion = settingKey[String]("ver")
ghVersion := s"v${version.value}-$osName"

val gitTagCmd = settingKey[String]("git tag command")
gitTagCmd := s"""git tag -a ${ghVersion.value} -m "release ${ghVersion.value}""""

//val gitTagCmd = s"""git tag -a $ver -m "release $ver""""
val ghCreateRelease = settingKey[String]("Create release")
ghCreateRelease := s"gh release create $$ghVersion.value}"

val ghUploadRelease = SettingKey[String]("Upload release")
ghUploadRelease := s"gh release upload ${ghVersion.value} ${(Universal / packageBin).value} --clobber -R dicklieber/rotatorcontrol"
*/


val ghRelease = taskKey[Unit]("send stuff to github")

ghRelease := {
  val log = streams.value.log
  log.info("=========ghRelease=========")

  val relVersion = s"v${version.value}-$osName"
  val pubArtifact: File = (Universal / packageBin).value

  val github = Paths.get("github.sh")
  log.debug(s"github path: $github")
  val abs: File = github.toAbsolutePath.toFile
  log.debug(s"github abs: $abs")

  log.debug(s"relVersion: $relVersion")
  log.debug(s"pubArtifact: $pubArtifact")

  val cmd = s"$abs $relVersion $pubArtifact"
  log.debug((s"cmd: $cmd"))
  Process(cmd).lineStream ! log
  log.info(s"\tcmd: $cmd done")


}


resolvers +=
  "ReposiliteXYZZY" at "http://127.0.0.1:8080/releases"

//credentials += Credentials("Reposilite", "127.0.0.1", "wa9nnn-deploy", "T/d7hlJWwdYMIj1GxmmVIB3IwuZ4X1FfZq7KDCtgbrjpTvBwLdxT2mSYGkfW025F")
credentials += Credentials(Path.userHome / ".sbt" / "credentials-reposolite")

publishTo := Some(("ReposilitePLUGH" at "http://194.113.64.105:8080/releases").withAllowInsecureProtocol(true))
//publishTo := Some(("ReposilitePLUGH" at "http://127.0.0.1:8080/releases").withAllowInsecureProtocol(true))

