scalaVersion := "2.12.6"

name := "final-tagless-talk-03Oct18"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.4.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7"
)

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "ammonite-ops" % "1.0.2",
  "org.scalamock" %% "scalamock" % "4.1.0" % Test,
  "org.apache.commons" % "commons-io" % "1.3.2" % "test",
  "commons-codec" % "commons-codec" % "1.10" % "test",
  "org.jasypt" % "jasypt" % "1.9.2" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.mchange" % "c3p0" % "0.9.5.2" % "test",
  "net.s_mach" %% "concurrent" % "2.0.0" % "test",
  "com.h2database" % "h2" % "1.4.192" % "test"
)
