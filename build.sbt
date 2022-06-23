inThisBuild(
  List(
    organization     := "io.github.987nabil",
    scalaVersion     := "2.13.8",
    organizationName := "Nabil Abdel-Hafeez",
    startYear        := Some(2022),
    homepage         := Some(url("https://github.com/987nabil/gar-coursier")),
    licenses         := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "987Nabil",
        "Nabil Abdel-Hafeez",
        "987nabil@gmail.com",
        url("https://github.com/987Nabil"),
      )
    ),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository     := "https://s01.oss.sonatype.org/service/local",
  )
)

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")

ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.Equals(Ref.Branch("master")),
  RefPredicate.StartsWith(Ref.Tag("v")),
)

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Run(
    List("sbt ci-release"),
    name = Some("Publish JARs"),
    env = Map(
      "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}",
    ),
  )
)

lazy val root = (project in file("."))
  .settings(
    name                                     := "gar-coursier",
    libraryDependencies += "com.google.cloud" % "google-cloud-storage" % "2.8.1",
  )
