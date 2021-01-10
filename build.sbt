lazy val server = project
  .settings(commonSettings)
  .settings(
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.2.2",
      "com.typesafe.akka" %% "akka-stream" % "2.6.10",
      "com.vmunier" %% "scalajs-scripts" % "1.1.4",
      "com.lihaoyi" %%% "scalatags" % "0.9.2"
    ),
    WebKeys.packagePrefix in Assets := "public/",
    managedClasspath in Runtime += (packageBin in Assets).value
  )
  .enablePlugins(JavaAppPackaging, WebScalaJSBundlerPlugin)
  .dependsOn(sharedJvm)

lazy val client = project
  .settings(commonSettings)
  .settings(
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0",
      "com.github.fdietze.scala-js-d3v4" %%% "scala-js-d3v4" % "af70ae6"
    ),
    scalaJSUseMainModuleInitializer := true
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(sharedJs)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.13.1",
  organization := "com.example"
)

