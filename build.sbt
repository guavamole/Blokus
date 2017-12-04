name := "blokusss"
 
version := "1.0" 
      
lazy val `blokusss` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.17",
  "com.typesafe.akka" %% "akka-stream" % "2.4.17",
  "org.scalaz" %% "scalaz-core" % "7.2.10",
  "org.scalaz" %% "scalaz-concurrent" % "7.2.10",
  "org.json4s" %% "json4s-native" % "3.5.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.google.inject" % "guice" % "4.0"
)
unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      