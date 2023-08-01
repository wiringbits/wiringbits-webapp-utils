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

val playJson = "2.10.0-RC5"
val sttp = "3.5.0"

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
lazy val baseLibSettings: Project => Project = _.settings(
  scalacOptions ++= {
    Seq(
      "-encoding",
      "UTF-8",
      "-feature",
      "-language:implicitConversions"
      // disabled during the migration
      // "-Xfatal-warnings"
    ) ++
      (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) =>
          Seq(
            "-unchecked",
            "-source:3.0-migration"
          )
        case _ =>
          Seq(
            "-deprecation",
            "-Xfatal-warnings",
            "-Wunused:imports,privates,locals",
            "-Wvalue-discard"
          )
      })
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
          "-language:implicitConversions"
          // disabled during the migration
          // "-Xfatal-warnings"
        ) ++
          (CrossVersion.partialVersion(scalaVersion.value) match {
            case Some((3, _)) =>
              Seq(
                "-unchecked",
                "-source:3.0-migration"
              )
            case _ =>
              Seq(
                "-deprecation",
                "-Xfatal-warnings",
                "-Wunused:imports,privates,locals",
                "-Wvalue-discard",
                "-Ymacro-annotations"
              )
          })
      },
      libraryDependencies ++= Seq(
        "io.github.cquiroz" %%% "scala-java-time" % "2.3.0",
        "org.scala-js" %%% "scala-js-macrotask-executor" % "1.1.1",
        "com.olvind.st-material-ui" %%% "st-material-ui-icons-slinky" % "5.11.16",
        "com.alexitc" %%% "sjs-material-ui-facade" % "0.2.0"

      )
    )

// specify versions for all of reacts dependencies
lazy val reactNpmDeps: Project => Project =
  _.settings(
    stTypescriptVersion := "3.9.3",
    stIgnore += "react-proxy",
    Compile / npmDependencies ++= Seq(
      "react" -> "17.0.0",
      "react-dom" -> "17.0.0",
      "@types/react" -> "17.0.0",
      "@types/react-dom" -> "17.0.0",
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
        "com.typesafe.play" %% "play-jdbc" % "2.8.19",
        "com.google.inject" % "guice" % "5.1.0"
      ),
      // test
      libraryDependencies ++= Seq(
        "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
        "org.mockito" %% "mockito-scala" % "1.17.14" % Test,
        "org.mockito" %% "mockito-scala-scalatest" % "1.17.14" % Test
      )
    )
}

lazy val scalablytypedFacades = (project in file("scalablytyped-facades"))
  .configure(_.enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterGenSourcePlugin))
  .settings(
    scalaVersion := "3.2.2",
    name := "scalablytyped-facades",
    useYarn := true,
    Test / requireJsDomEnv := true,
    stTypescriptVersion := "3.9.3",
    stOutputPackage := "net.wiringbits.facades",
    // material-ui is provided by a pre-packaged library
    stIgnore ++= List(
      "@mui/material",
      "@mui/styles",
      "@mui/icons-material",
      "react-router",
      "react-router-dom"
    ),
    Compile / npmDependencies ++= Seq(
      "@mui/material"->"5.11.15",
      "@mui/styles" -> "5.11.13", // note: version 4 is not supported yet
      "@mui/icons-material"->"5.11.11",
      "@types/classnames" -> "2.2.10",
      "react-router" -> "5.1.2",
      "react-router-dom" -> "5.1.2"
    ),
    stFlavour := Flavour.Slinky,
    stReactEnableTreeShaking := Selection.All,
    stUseScalaJsDom := true,
    stMinimize := Selection.AllExcept("@types/classnames"),
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
    scalaVersion := "3.2.2",
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
  .dependsOn(webappCommon.js, scalablytypedFacades)
  .settings(
    scalaVersion := "3.2.2",
    name := "slinky-utils",
    Test / fork := false // sjs needs this to run tests
  )

lazy val root = (project in file("."))
  .aggregate(
    scalablytypedFacades,
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