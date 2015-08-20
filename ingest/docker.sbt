enablePlugins(JavaAppPackaging, DockerPlugin)

dockerBaseImage    := "java:openjdk-8-jdk"
dockerExposedPorts := Seq(8080)

dockerExposedVolumes in Docker := Seq("/var/run/docker.sock")

packageName in Docker := packageName.value
version in Docker := version.value
