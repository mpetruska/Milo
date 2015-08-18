import buildUtils._

commonSettings(
  name         := "Milo",
  description  := "Experimental POC computation engine for real-time query processing",
  version      := "0.0.1",
  scalaVersion := "2.11.7"
)

// Projects
lazy val ingest = project.in(file("ingest"))
