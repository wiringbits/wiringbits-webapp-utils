ThisBuild / scalaVersion := "2.13.7"
ThisBuild / versionScheme := Some("early-semver")
// For all Sonatype accounts created on or after February 2021
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

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

val playJson = "2.9.2"
val sttp = "3.3.18"

val consoleDisabledOptions = Seq("-Xfatal-warnings", "-Ywarn-unused", "-Ywarn-unused-import")

// Used only by the server
// TODO: Reuse it in all projects
lazy val baseServerSettings: Project => Project = {
  _.settings(
    scalacOptions ++= Seq(
      "-Werror",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-target:jvm-1.8",
      "-encoding",
      "UTF-8",
      "-Xsource:3",
      "-Wconf:src=src_managed/.*:silent",
      "-Xlint:missing-interpolator",
      "-Xlint:adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Ywarn-unused"
    ),
    Compile / doc / scalacOptions ++= Seq("-no-link-warnings"),
    // Some options are very noisy when using the console and prevent us using it smoothly, let's disable them
    Compile / console / scalacOptions ~= (_.filterNot(consoleDisabledOptions.contains))
  )
}

// Used only by the lib projects
lazy val baseLibSettings: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      Test / fork := false, // sjs needs this to run tests
      scalacOptions ++= Seq(
        "-deprecation", // Emit warning and location for usages of deprecated APIs.
        "-encoding",
        "utf-8", // Specify character encoding used by source files.
        "-explaintypes", // Explain type errors in more detail.
        "-feature", // Emit warning and location for usages of features that should be imported explicitly.
        "-unchecked" // Enable additional warnings where generated code depends on assumptions.
      ),
      libraryDependencies ++= Seq(
        "org.scalatest" %%% "scalatest" % "3.2.10" % Test
      )
    )

// Used only by the lib projects
lazy val baseWebSettings: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      scalacOptions += "-Ymacro-annotations",
      libraryDependencies ++= Seq(
        "io.github.cquiroz" %%% "scala-java-time" % "2.3.0",
        "org.scala-js" %%% "scala-js-macrotask-executor" % "1.0.0",
        "com.alexitc" %%% "sjs-material-ui-facade" % "0.1.6"
      )
    )

// specify versions for all of reacts dependencies
lazy val reactNpmDeps: Project => Project =
  _.settings(
    stTypescriptVersion := "3.9.3",
    stIgnore += "react-proxy",
    Compile / npmDependencies ++= Seq(
      "react" -> "16.13.1",
      "react-dom" -> "16.13.1",
      "@types/react" -> "16.9.42",
      "@types/react-dom" -> "16.9.8",
      "csstype" -> "2.6.11",
      "@types/prop-types" -> "15.7.3",
      "react-proxy" -> "1.1.8"
    )
  )

lazy val withCssLoading: Project => Project =
  _.settings(
    /* custom webpack file to include css */
    Compile / webpackConfigFile := Some((ThisBuild / baseDirectory).value / "custom.webpack.config.js"),
    Test / webpackConfigFile := None, // it is important to avoid the custom webpack config in tests to get them passing
    Compile / npmDevDependencies ++= Seq(
      "webpack-merge" -> "4.2.2",
      "css-loader" -> "3.4.2",
      "style-loader" -> "1.1.3",
      "file-loader" -> "5.1.0",
      "url-loader" -> "3.0.0"
    )
  )

lazy val bundlerSettings: Project => Project =
  _.settings(
    Compile / fastOptJS / webpackExtraArgs += "--mode=development",
    Compile / fullOptJS / webpackExtraArgs += "--mode=production",
    Compile / fastOptJS / webpackDevServerExtraArgs += "--mode=development",
    Compile / fullOptJS / webpackDevServerExtraArgs += "--mode=production"
  )

// Used only by play-based projects
lazy val playSettings: Project => Project = {
  _.enablePlugins(PlayScala)
    .disablePlugins(PlayLayoutPlugin)
    .settings(
      // docs are huge and unnecessary
      Compile / doc / sources := Nil,
      Compile / doc / scalacOptions ++= Seq(
        "-no-link-warnings"
      ),
      // remove play noisy warnings
      play.sbt.routes.RoutesKeys.routesImport := Seq.empty,
      libraryDependencies ++= Seq(
        evolutions,
        "com.typesafe.play" %% "play-jdbc" % "2.8.13",
        "com.google.inject" % "guice" % "5.0.1"
      ),
      // test
      libraryDependencies ++= Seq(
        "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
        "org.mockito" %% "mockito-scala" % "1.16.49" % Test,
        "org.mockito" %% "mockito-scala-scalatest" % "1.16.49" % Test
      )
    )
}

lazy val scalablytypedFacades = (project in file("scalablytyped-facades"))
  .configure(_.enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterGenSourcePlugin))
  .settings(
    name := "scalablytyped-facades",
    useYarn := true,
    Test / requireJsDomEnv := true,
    stTypescriptVersion := "3.9.3",
    stOutputPackage := "net.wiringbits.facades",
    // material-ui is provided by a pre-packaged library
    stIgnore ++= List("@material-ui/core", "@material-ui/styles", "@material-ui/icons"),
    Compile / npmDependencies ++= Seq(
      "@material-ui/core" -> "3.9.4", // note: version 4 is not supported yet
      "@material-ui/styles" -> "3.0.0-alpha.10", // note: version 4 is not supported yet
      "@material-ui/icons" -> "3.0.2",
      "@types/classnames" -> "2.2.10",
      "react-router" -> "5.1.2",
      "@types/react-router" -> "5.1.2",
      "react-router-dom" -> "5.1.2",
      "@types/react-router-dom" -> "5.1.2"
    ),
    stFlavour := Flavour.Slinky,
    stReactEnableTreeShaking := Selection.All,
    stUseScalaJsDom := true,
    stMinimize := Selection.AllExcept("@types/classnames", "@types/react-router", "@types/react-router-dom"),
    // docs are huge and unnecessary
    Compile / doc / sources := Nil,
    // disabled because it somehow triggers many warnings
    scalaJSLinkerConfig ~= (_.withSourceMap(false))
  )

/** The common stuff for the server/client modules
  */
lazy val webappCommon = (crossProject(JSPlatform, JVMPlatform) in file("webapp-common"))
  .configure(baseLibSettings)
  .settings(
    name := "webapp-common"
  )
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin))
  .jvmSettings(
    libraryDependencies ++= Seq(
      // TODO: This shouldn't depend in play-json but I'm leaving it for simplicity
      "com.typesafe.play" %% "play-json" % playJson
    )
  )
  .jsSettings(
    stUseScalaJsDom := true,
    Compile / stMinimize := Selection.All,
    libraryDependencies ++= Seq(
      // TODO: This shouldn't depend in play-json but I'm leaving it for simplicity
      "com.typesafe.play" %%% "play-json" % playJson
    )
  )

/** Just the API side for the admin-data-explorer modules
  */
lazy val adminDataExplorerApi = (crossProject(JSPlatform, JVMPlatform) in file("admin-data-explorer-api"))
  .configure(baseLibSettings)
  .dependsOn(webappCommon)
  .settings(
    name := "admin-data-explorer-api"
  )
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin))
  .jvmSettings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % playJson,
      "com.softwaremill.sttp.client3" %% "core" % sttp
    )
  )
  .jsSettings(
    stUseScalaJsDom := true,
    Compile / stMinimize := Selection.All,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %%% "play-json" % playJson,
      "com.softwaremill.sttp.client3" %%% "core" % sttp
    )
  )

/** Utils specific to slinky
  */
lazy val slinkyUtils = (project in file("slinky-utils"))
  .configure(baseLibSettings, baseWebSettings)
  .configure(_.enablePlugins(ScalaJSBundlerPlugin))
  .dependsOn(adminDataExplorerApi.js, webappCommon.js, scalablytypedFacades)
  .settings(
    name := "slinky-utils"
  )

// shared on the ui only
lazy val adminDataExplorerSlinky = (project in file("admin-data-explorer-slinky"))
  .configure(baseLibSettings, baseWebSettings)
  .configure(_.enablePlugins(ScalaJSBundlerPlugin))
  .dependsOn(adminDataExplorerApi.js, slinkyUtils, scalablytypedFacades)
  .settings(
    name := "admin-data-explorer-slinky"
  )

/** Includes the specific stuff to run the data explorer server side (play-specific)
  */
lazy val adminDataExplorerPlayServer = (project in file("admin-data-explorer-play-server"))
  .dependsOn(adminDataExplorerApi.jvm, webappCommon.jvm)
  .configure(baseServerSettings, playSettings)
  .settings(
    name := "admin-data-explorer-play-server",
    fork := true,
    Test / fork := true, // allows for graceful shutdown of containers once the tests have finished running
    libraryDependencies ++= Seq(
      "org.playframework.anorm" %% "anorm" % "2.6.10",
      "com.typesafe.play" %% "play" % "2.8.13",
      "com.typesafe.play" %% "play-json" % "2.9.2",
      "org.postgresql" % "postgresql" % "42.3.1",
      "com.github.jwt-scala" %% "jwt-core" % "9.0.3",
      "de.svenkubiak" % "jBCrypt" % "0.4.3",
      "commons-validator" % "commons-validator" % "1.7",
      "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.39.12" % "test",
      "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.39.12" % "test",
      "com.softwaremill.sttp.client3" %% "core" % sttp % "test",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % sttp % "test"
    )
  )

lazy val root = (project in file("."))
  .aggregate(
    scalablytypedFacades,
    webappCommon.jvm,
    webappCommon.js,
    adminDataExplorerApi.jvm,
    adminDataExplorerApi.js,
    slinkyUtils,
    adminDataExplorerSlinky,
    adminDataExplorerPlayServer
  )
  .settings(
    name := "wiringbits-webapp-utils",
    publish := {},
    publishLocal := {},
    publish / skip := true
  )
