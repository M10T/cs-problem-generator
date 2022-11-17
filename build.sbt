val scala3Version = "3.2.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "cs-problem-generator",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
  )


libraryDependencies += "com.lihaoyi" %% "cask" % "0.8.3";
libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.12.0";
dependencyOverrides += "com.lihaoyi" %% "geny" % "1.0.0"