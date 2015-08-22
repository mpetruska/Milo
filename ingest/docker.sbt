enablePlugins(JavaAppPackaging, DockerPlugin)

dockerBaseImage    := "java:openjdk-8-jre"
dockerExposedPorts := Seq(8080)
dockerRepository   := Some("4lex1v")

inConfig(Docker)(Seq(
  version              := version.value,
  packageName          := packageName.value,
  dockerExposedVolumes := Seq("/var/run/docker.sock")
))
