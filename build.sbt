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
        "com.alexitc" %%% "sjs-material-ui-facade" % "0.2.0"
      )
    )

lazy val scalablytypedFacades = (project in file("scalablytyped-facades"))
  .configure(_.enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterGenSourcePlugin))
  .settings(
    scalaVersion := "2.13.8",
    crossScalaVersions := Seq("2.13.8", "3.2.2"),
    name := "scalablytyped-facades",
    useYarn := true,
    Test / requireJsDomEnv := true,
    stTypescriptVersion := "3.9.3",
    stOutputPackage := "net.wiringbits.facades",
    // material-ui is provided by a pre-packaged library
    stIgnore ++= List(
      "@material-ui/core",
      "@material-ui/styles",
      "@material-ui/icons",
      "react-router",
      "react-router-dom"
    ),
    Compile / npmDependencies ++= Seq(
      "@material-ui/core" -> "3.9.4", // note: version 4 is not supported yet
      "@material-ui/styles" -> "3.0.0-alpha.10", // note: version 4 is not supported yet
      "@material-ui/icons" -> "3.0.2",
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
    scalaVersion := "2.13.8",
    crossScalaVersions := Seq("2.13.8", "3.2.2"),
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
    scalaVersion := "2.13.8",
    crossScalaVersions := Seq("2.13.8", "3.2.2"),
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
