ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    organization                             := "com.rewe-digital",
    name                                     := "gcs-coursier",
    libraryDependencies += "com.google.cloud" % "google-cloud-storage" % "2.4.1",
  )
