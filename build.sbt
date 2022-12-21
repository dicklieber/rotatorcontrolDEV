import com.typesafe.sbt.packager.SettingsHelper.makeDeploymentSettings
import sbt.Def

import scala.sys.process._

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "rotatorcontrol"
  )

Universal / mappings := (Universal / mappings).value

Universal / javaOptions ++= Seq(
  "-java-home ${app_home}/../jre"
)

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

//deploymentSettings

// wix build information
wixProductId := "fc424cc6-73eb-11ed-a8c1-270fc9539747"
wixProductUpgradeId := "1d67f32e-73ec-11ed-a75e-9b5a53518d4c"
jlinkIgnoreMissingDependency := JlinkIgnore.everything

resolvers += ("Reposilite" at "http://194.113.64.105:8080/releases")
  .withAllowInsecureProtocol(true)

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux") => "linux"
  case n if n.startsWith("Mac") => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ =>
    throw new Exception("Unknown platform!")
}

lazy val javaFXModules = {

  // Create dependencies for JavaFX modules
  Seq("base", "controls", "graphics", "media")
    .map(m => "org.openjfx" % s"javafx-$m" % "15.0.1" classifier osName)
}

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

publish / skip := false

import ReleaseTransformations._

// ...

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,              // : ReleaseStep
  inquireVersions,                        // : ReleaseStep
  runClean,                               // : ReleaseStep
  runTest,                                // : ReleaseStep
  setReleaseVersion,                      // : ReleaseStep
  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
  tagRelease,                             // : ReleaseStep
  publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
  setNextVersion,                         // : ReleaseStep
  commitNextVersion,                      // : ReleaseStep
  pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
)



val ghRelease = taskKey[Unit]("Create release")


ghRelease := {
  val ver = s"v${version.value}"
  println(s"ver: $ver")
  val gitTagCmd = s"""git tag -a $ver -m "release $ver""""
  println(s"gitTagCmd: $gitTagCmd")
  Process(gitTagCmd).run()
  val ghCmd = s"gh release create $ver"
  println(s"ghCmd: $ghCmd")
  Process(ghCmd).run()
}


val ghReleaseUpload: TaskKey[Unit] = taskKey[Unit]("Create release")


ghReleaseUpload := {
  val ver = s"v${version.value}"
  println(s"ver: $ver")
  val packageBinFile: File = (Universal / packageBin).value
  val ghCmd = s"gh release upload $ver $packageBinFile --clobber -R dicklieber/rotatorcontrol"
  println(s"ghCmd: $ghCmd")
  Process(ghCmd).run()
}

resolvers +=
  "ReposiliteXYZZY" at  "http://127.0.0.1:8080/releases"

//credentials += Credentials("Reposilite", "127.0.0.1", "wa9nnn-deploy", "T/d7hlJWwdYMIj1GxmmVIB3IwuZ4X1FfZq7KDCtgbrjpTvBwLdxT2mSYGkfW025F")
credentials += Credentials(Path.userHome / ".sbt" / "credentials-reposolite")

publishTo := Some(("ReposilitePLUGH" at "http://194.113.64.105:8080/releases").withAllowInsecureProtocol(true))
//publishTo := Some(("ReposilitePLUGH" at "http://127.0.0.1:8080/releases").withAllowInsecureProtocol(true))

