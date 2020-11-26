ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "dev.ligature"
ThisBuild / organizationName := "Ligature"

val dottyVersion = "3.0.0-M1"

lazy val root = (project in file("."))
  .settings(
    name := "ligature-mock",

    scalaVersion := dottyVersion,

    libraryDependencies += "dev.ligature" %% "ligature" % "0.1.0-SNAPSHOT",
    libraryDependencies += "dev.ligature" %% "ligature-test-suite" % "0.1.0-SNAPSHOT" % Test,
    testFrameworks += new TestFramework("munit.Framework")
  )
