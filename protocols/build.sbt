libraryDependencies ++= Seq(
  "com.typesafe.akka"   %% "akka-actor"   % "2.3.12",
  "com.squants"         %% "squants"      % "0.5.3",
  "org.spire-math"      %% "cats"         % "0.1.2",
  // Test
  "org.scalacheck"      %% "scalacheck"   % "1.12.4" % Test,
  "org.scalatest"       %% "scalatest"    % "2.2.4"  % Test
)

com.trueaccord.scalapb.ScalaPbPlugin.protobufSettings
