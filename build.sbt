organization := "com.github.losizm"
name         := "little-json"
version      := "7.0.0-SNAPSHOT"
description  := "The Scala library that provides extension methods to javax.json"
homepage     := Some(url("https://github.com/losizm/little-json"))
licenses     := List("Apache License, Version 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion       := "2.13.4"
crossScalaVersions := Seq("2.12.12")

scalacOptions ++= Seq("-deprecation", "-feature", "-Xcheckinit")

Compile / doc / scalacOptions ++= Seq(
  "-doc-title"  , name.value,
  "-doc-version", version.value
)

unmanagedSourceDirectories in Compile += {
  (sourceDirectory in Compile).value / s"scala-${scalaBinaryVersion.value}"
}

libraryDependencies ++= Seq(
  "javax.json"    %  "javax.json-api" % "1.1.4" % "provided",
  "org.glassfish" %  "javax.json"     % "1.1.4" % "test",
  "org.scalatest" %% "scalatest"      % "3.2.0" % "test"
)

developers := List(
  Developer(
    id    = "losizm",
    name  = "Carlos Conyers",
    email = "carlos.conyers@hotmail.com",
    url   = url("https://github.com/losizm")
  )
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/losizm/little-json"),
    "scm:git@github.com:losizm/little-json.git"
  )
)

publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org"

  isSnapshot.value match {
    case true  => Some("snaphsots" at s"$nexus/content/repositories/snapshots")
    case false => Some("releases"  at s"$nexus/service/local/staging/deploy/maven2")
  }
}
