name := "little-json"
version := "2.7.0-SNAPSHOT"
organization := "com.github.losizm"

scalaVersion := "2.12.8"
scalacOptions ++= Seq("-deprecation", "-feature", "-Xcheckinit")

libraryDependencies ++= Seq(
  "javax.json"    %  "javax.json-api" % "1.1.4" % "provided",
  "org.glassfish" %  "javax.json"     % "1.1.4" % "test",
  "org.scalatest" %% "scalatest"      % "3.0.5" % "test"
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/losizm/little-json"),
    "scm:git@github.com:losizm/little-json.git"
  )
)

developers := List(
  Developer(
    id    = "losizm",
    name  = "Carlos Conyers",
    email = "carlos.conyers@hotmail.com",
    url   = url("https://github.com/losizm")
  )
)

description := "The Scala library that provides extension methods to javax.json"
licenses := List("Apache License, Version 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("https://github.com/losizm/little-json"))

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org"
  if (isSnapshot.value) Some("snaphsots" at s"$nexus/content/repositories/snapshots")
  else Some("releases" at s"$nexus/service/local/staging/deploy/maven2")
}

publishMavenStyle := true
