
val akkaVersion = "2.3.12"

lazy val root = (project in file("."))
  .settings(
    name := "tcp-server-sample",
    version := "0.1",
    scalaVersion := "2.11.5",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-kernel" % akkaVersion
    ),
    mainClass in Compile := Some("MicroKernel")
  )
