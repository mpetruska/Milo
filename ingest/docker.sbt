enablePlugins(JavaAppPackaging, DockerPlugin)

dockerBaseImage    := "java:openjdk-8-jre"
dockerExposedPorts := Seq(8080)
dockerRepository   := Some("4lex1v")
dockerUpdateLatest := true

inConfig(Docker)(Seq(
  version              := version.value,
  maintainer           := "Aleksandr Ivanov, 4lex1v@gmail.com",
  packageName          := packageName.value,
  dockerExposedVolumes := Seq("/var/run/docker.sock")
))
