scalaVersion := "2.12.6"

name := "final-tagless-talk-03Oct18"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.4.0"
)

libraryDependencies ++= Seq(
  "org.scalamock" %% "scalamock" % "4.1.0" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
