// Scala versions
val scala213 = "2.13.5"
val scala212 = "2.12.13"
val scala211 = "2.11.12"
val scala3 = List("3.0.1")
val scala2 = List(scala213, scala212, scala211)
val allScalaVersions = scala3 ::: scala2
val scalaJVMVersions = allScalaVersions
val scalaJSVersions = allScalaVersions
val scalaNativeVersions = scala2

name := "testy"
ThisBuild / organization := "com.outr"
ThisBuild / version := "1.0.7"
ThisBuild / scalaVersion := scala213
ThisBuild / scalacOptions ++= Seq("-unchecked", "-deprecation")
ThisBuild / javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

ThisBuild / publishTo := sonatypePublishTo.value
ThisBuild / sonatypeProfileName := "com.outr"
ThisBuild / licenses := Seq("MIT" -> url("https://github.com/outr/testy/blob/master/LICENSE"))
ThisBuild / sonatypeProjectHosting := Some(xerial.sbt.Sonatype.GitHubHosting("outr", "testy", "matt@outr.com"))
ThisBuild / homepage := Some(url("https://github.com/outr/testy"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/outr/testy"),
    "scm:git@github.com:outr/testy.git"
  )
)
ThisBuild / developers := List(
  Developer(id="darkfrog", name="Matt Hicks", email="matt@matthicks.com", url=url("http://matthicks.com"))
)

ThisBuild / testOptions += Tests.Argument("-oD")

// Dependency versions
val collectionCompatVersion: String = "2.4.4"
val munitVersion: String = "0.7.26"

// set source map paths from local directories to github path
val sourceMapSettings = List(
  scalacOptions ++= git.gitHeadCommit.value.map { headCommit =>
    val local = baseDirectory.value.toURI
    val remote = s"https://raw.githubusercontent.com/outr/testy/$headCommit/"
    s"-P:scalajs:mapSourceURI:$local->$remote"
  }
)

lazy val root = project.in(file("."))
  .aggregate(
    core.js, core.jvm, core.native
  )
  .settings(
    name := "testy",
    publish := {},
    publishLocal := {}
  )

lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .settings(
    name := "testy",
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % munitVersion
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    libraryDependencies ++= (
      if (scalaVersion.value.startsWith("3.0")) {
        Nil
      } else {
        Seq(
          "org.scala-lang.modules" %% "scala-collection-compat" % collectionCompatVersion,
          "org.scala-lang" % "scala-reflect" % scalaVersion.value
        )
      }
    ),
    Compile / unmanagedSourceDirectories ++= {
      val major = if (scalaVersion.value.startsWith("3.0")) "-3" else "-2"
      List(CrossType.Pure, CrossType.Full).flatMap(
        _.sharedSrcDir(baseDirectory.value, "main").toList.map(f => file(f.getPath + major))
      )
    }
  )
  .jsSettings(
    crossScalaVersions := scalaJSVersions,
    Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
  )
  .jvmSettings(
    crossScalaVersions := scalaJVMVersions
  )
  .nativeSettings(
    scalaVersion := scala213,
    crossScalaVersions := scalaNativeVersions
  )
