import buildUtils._

commonSettings(
  name         := "Milo",
  description  := "Experimental POC computation engine for real-time query processing",
  version      := "0.0.1",
  scalaVersion := "2.11.7",

  shellPrompt  := { Project.extract(_).currentProject.id + " >> " }
)

// Projects
lazy val extensions   = project.in(file("extensions"))
lazy val protocols    = project.in(file("protocols")).dependsOn(extensions)
lazy val ingest       = project.in(file("ingest")).dependsOn(protocols)

