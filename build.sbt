import buildUtils._

commonSettings(
  name         := "Milo",
  description  := "Experimental POC computation engine for realtime query processing",
  version      := "0.0.1",
  scalaVersion := "2.11.7",

  shellPrompt  := { Project.extract(_).currentProject.id + " >> " }
)

// Projects
lazy val protocols = project.in(file("protocols"))
lazy val fleet     = project.in(file("fleet")).dependsOn(protocols)
lazy val ingest    = project.in(file("ingest")).dependsOn(protocols)

