ThisBuild / versionScheme := Some("early-semver")
// For all Sonatype accounts created on or after February 2021
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / scalaVersion := "3.3.3"

inThisBuild(
  List(
    organization := "net.wiringbits",
    name := "wiringbits-webapp-utils",
    homepage := Some(url("https://github.com/wiringbits/wiringbits-webapp-utils")),
    licenses := List("MIT" -> url("https://www.opensource.org/licenses/mit-license.html")),
    developers := List(
      Developer(
        "AlexITC",
        "Alexis Hernandez",
        "alexis22229@gmail.com",
        url("https://wiringbits.net")
      )
    )
  )
)

resolvers += Resolver.sonatypeRepo("releases")

val playJson = "2.10.0-RC5"
val stMaterialUi = "5.11.16"

// Used only by the lib projects
lazy val baseLibSettings: Project => Project = _.settings(
  scalacOptions ++= {
    Seq(
      "-encoding",
      "UTF-8",
      "-feature",
      "-language:implicitConversions",
      "-unchecked",
      "-source:3.0-migration"
    )
  },
  libraryDependencies ++= Seq(
    "org.scalatest" %%% "scalatest" % "3.2.15" % Test
  )
)

// Used only by the lib projects
lazy val baseWebSettings: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      scalacOptions ++= {
        Seq(
          "-encoding",
          "UTF-8",
          "-feature",
          "-language:implicitConversions",
          "-unchecked",
          "-source:3.0-migration"
        )
      },
      libraryDependencies ++= Seq(
        "io.github.cquiroz" %%% "scala-java-time" % "2.3.0",
        "org.scala-js" %%% "scala-js-macrotask-executor" % "1.1.1",
        "com.olvind.st-material-ui" %%% "st-material-ui-icons-slinky" % stMaterialUi
      )
    )

/** The common stuff for the server/client modules
  */
lazy val webappCommon = (crossProject(JSPlatform, JVMPlatform) in file("webapp-common"))
  .configure(baseLibSettings)
  .settings(
    name := "webapp-common"
  )
  .jsConfigure(_.enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin))
  .jvmSettings(
    libraryDependencies ++= Seq(
      // TODO: This shouldn't depend in play-json but I'm leaving it for simplicity
      "com.typesafe.play" %% "play-json" % playJson
    )
  )
  .jsSettings(
    stUseScalaJsDom := true,
    Test / fork := false, // sjs needs this to run tests
    Compile / stMinimize := Selection.All,
    libraryDependencies ++= Seq(
      // TODO: This shouldn't depend in play-json but I'm leaving it for simplicity
      "com.typesafe.play" %%% "play-json" % playJson
    )
  )

/** Utils specific to slinky
  */
lazy val slinkyUtils = (project in file("slinky-utils"))
  .configure(baseLibSettings, baseWebSettings)
  .configure(_.enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin))
  .dependsOn(webappCommon.js)
  .settings(
    name := "slinky-utils",
    Test / fork := false, // sjs needs this to run tests
    useYarn := true,
    Compile / npmDependencies ++= Seq(
      "react" -> "18.2.0",
      "react-dom" -> "18.2.0",
      "@mui/material" -> "5.11.16",
      "@mui/icons-material" -> "5.11.16",
      "@mui/joy" -> "5.0.0-alpha.74",
      "@emotion/react" -> "11.10.6",
      "@emotion/styled" -> "11.10.6",
      "react-router" -> "5.1.2",
      "react-router-dom" -> "5.1.2"
    )
  )

lazy val root = (project in file("."))
  .aggregate(
    webappCommon.jvm,
    webappCommon.js,
    slinkyUtils
  )
  .settings(
    name := "wiringbits-webapp-utils",
    publish := {},
    publishLocal := {},
    publish / skip := true
  )
