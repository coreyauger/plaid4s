name := "plaid4s"

version := "0.0.1-SNAPSHOT"

organization := "io.surfkit"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

scalaVersion := "2.12.3"

fork := true

val akkaV = "2.5.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka"       %% "akka-actor"                 % akkaV,
  "com.typesafe.akka"       %% "akka-stream"                % akkaV,
  "com.typesafe.akka"       %% "akka-http"                  % "10.0.9",
  "com.typesafe.play"       %% "play-json"                  % "2.6.9",
  "com.typesafe.play"       %% "play-json-joda"             % "2.6.0",
  "de.heikoseeberger"       %% "akka-http-play-json"        % "1.17.0"
)

homepage := Some(url("http://www.surfkit.io/"))

licenses += ("MIT License", url("http://www.opensource.org/licenses/mit-license.php"))

