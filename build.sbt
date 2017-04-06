name := "blokeus"

version := "1.0"

scalaVersion := "2.12.1"

resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Resolver.bintrayRepo("scalaz", "releases")
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.17",
  "com.typesafe.akka" %% "akka-stream" % "2.4.17",
  "org.scalaz" %% "scalaz-core" % "7.2.10",
  "org.scalaz" %% "scalaz-concurrent" % "7.2.10",
  "org.json4s" %% "json4s-native" % "3.5.1"
)
