// Scala versions
val scala213 = "2.13.5"
val scala212 = "2.12.13"
val scala211 = "2.11.12"
val scala3 = "3.0.0-RC1"
val scala2 = List(scala213, scala212, scala211)
val allScalaVersions = scala3 :: scala2
val scalaJVMVersions = allScalaVersions
val scalaJSVersions = allScalaVersions
val scalaNativeVersions = scala2

name := "testy"
organization in ThisBuild := "com.outr"
version in ThisBuild := "1.0.2-SNAPSHOT"
scalaVersion in ThisBuild := scala213
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
javacOptions in ThisBuild ++= Seq("-source", "1.8", "-target", "1.8")

publishTo in ThisBuild := sonatypePublishTo.value
sonatypeProfileName in ThisBuild := "com.outr"
licenses in ThisBuild := Seq("MIT" -> url("https://github.com/outr/testy/blob/master/LICENSE"))
sonatypeProjectHosting in ThisBuild := Some(xerial.sbt.Sonatype.GitHubHosting("outr", "testy", "matt@outr.com"))
homepage in ThisBuild := Some(url("https://github.com/outr/testy"))
scmInfo in ThisBuild := Some(
  ScmInfo(
    url("https://github.com/outr/testy"),
    "scm:git@github.com:outr/testy.git"
  )
)
developers in ThisBuild := List(
  Developer(id="darkfrog", name="Matt Hicks", email="matt@matthicks.com", url=url("http://matthicks.com"))
)

testOptions in ThisBuild += Tests.Argument("-oD")

// Dependency versions
val collectionCompatVersion: String = "2.4.3"
val munitVersion: String = "0.7.23"

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
      if (isDotty.value) {
        Nil
      } else {
        Seq(
          "org.scala-lang.modules" %% "scala-collection-compat" % collectionCompatVersion,
          "org.scala-lang" % "scala-reflect" % scalaVersion.value
        )
      }
    ),
    Compile / unmanagedSourceDirectories ++= {
      val major = if (isDotty.value) "-3" else "-2"
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
