resolvers ++= Seq(

)

libraryDependencies ++= Seq(
  // Typesage stack
  "com.typesafe.akka" %% "akka-actor"   % "2.3.12",
  "com.typesafe.akka" %% "akka-cluster" % "2.3.12",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.12" % "test",
  "com.typesafe"       % "config"       % "1.3.0",

  // Scodec
  "org.scodec" %% "scodec-bits" % "1.0.9",
  "org.scodec" %% "scodec-core" % "1.8.1",

  // Functional stuff
  "org.spire-math" %% "cats" % "0.1.2",

  // Test ecosystem
  "org.scalacheck" %% "scalacheck" % "1.12.4" % "test",
  "org.scalatest"  %% "scalatest"  % "2.2.4"  % "test"
)
