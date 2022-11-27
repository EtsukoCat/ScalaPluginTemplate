ThisBuild / version := "0.1.0"
ThisBuild / organization := "org.example"
ThisBuild / scalaVersion := "3.2.1"

resolvers in Global ++= Seq(
  "Sbt plugins" at "https://dl.bintray.com/sbt/sbt-plugin-releases",
  "Maven Central Server" at "https://repo1.maven.org/maven2",
  "TypeSafe Repository Releases" at "https://repo.typesafe.com/typesafe/releases/",
  "TypeSafe Repository Snapshots" at "https://repo.typesafe.com/typesafe/snapshots/",
  "jitpack" at "https://www.jitpack.io"
)

lazy val root = (project in file("."))
  .settings(
    name := "ScalaPluginTemplate",
    idePackagePrefix := Some("org.example"),
    assembly / assemblyJarName := "scala-plugin-template.jar"
  )
  .enablePlugins(AssemblyPlugin)

val CO = config("compileonly").hide
ivyConfigurations += CO

libraryDependencies += "com.github.Anuken" % "Mindustry" % "v140.4" % "compileonly"

Compile / unmanagedClasspath ++=
  update.value.select(configurationFilter("compileonly"))