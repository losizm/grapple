name := "little-json"
version := "1.0.0"
organization := "losizm"

scalaVersion := "2.12.6"
scalacOptions ++= Seq("-deprecation", "-feature", "-Xcheckinit")

libraryDependencies ++= Seq(
  "javax.json"    %  "javax.json-api" % "1.1.2" % "provided",
  "org.glassfish" %  "javax.json"     % "1.1.2" % "test",
  "org.scalatest" %% "scalatest"      % "3.0.5" % "test"
)
