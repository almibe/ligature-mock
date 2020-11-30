ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "dev.ligature"
ThisBuild / organizationName := "Ligature"

lazy val root = (project in file("."))
  .settings(
    name := "ligature-mock",
    scalaVersion := "3.0.0-M2",
    libraryDependencies += "dev.ligature" %% "ligature" % "0.1.0-SNAPSHOT",
    libraryDependencies += "dev.ligature" %% "ligature-test-suite" % "0.1.0-SNAPSHOT" % Test,
    testFrameworks += new TestFramework("munit.Framework")
  )
