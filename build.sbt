name := "akka-blog"
organization := "unlimited_works"
scalaVersion := "2.11.8"
parallelExecution in ThisBuild := false

version := "1.0"

lazy val versions = new {
  val finatra = "2.2.0"
  val guice = "4.0"
  val logback = "1.1.7"
}

assemblyMergeStrategy in assembly := {
  case "BUILD" => MergeStrategy.discard
  case other => MergeStrategy.defaultMergeStrategy(other)
}

libraryDependencies ++= Seq(
  "io.reactivex" %% "rxscala" % "0.26.2",
  "com.typesafe.akka" %% "akka-actor" % "2.4.8",
  "com.typesafe.akka" %% "akka-http-core"  % "2.4.8",
  "com.typesafe.akka" %% "akka-http-experimental"  % "2.4.8",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental"  % "2.4.8",
  "com.typesafe.akka" %% "akka-remote" % "2.4.8",
  "com.typesafe.akka" %% "akka-cluster" % "2.4.8",
  "org.postgresql" % "postgresql" % "9.4.1209",
  "org.scalactic" %% "scalactic" % "2.2.6",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",

  "org.mongodb.scala" %% "mongo-scala-driver" % "1.1.1",
  "net.liftweb" %% "lift-json" % "3.0-M8",

  "io.reactivex" %% "rxscala" % "0.26.2",

  "io.spray" %% "spray-can" % "1.3.3",
  "io.spray" %% "spray-routing" % "1.3.3",
//  "net.debasishg" %% "redisclient" % "3.0",
  "com.livestream" %% "scredis" % "2.0.6"

  //  "io.spray" % "spray-testkit" % "1.3.3" % "test"

  //finatra
//  "com.twitter" %% "finatra-http" % versions.finatra,
//  "com.twitter" %% "finatra-httpclient" % versions.finatra,
//  "ch.qos.logback" % "logback-classic" % versions.logback,
//
//  "com.twitter" %% "finatra-http" % versions.finatra % "test",
//  "com.twitter" %% "finatra-jackson" % versions.finatra % "test",
//  "com.twitter" %% "inject-server" % versions.finatra % "test",
//  "com.twitter" %% "inject-app" % versions.finatra % "test",
//  "com.twitter" %% "inject-core" % versions.finatra % "test",
//  "com.twitter" %% "inject-modules" % versions.finatra % "test",
//  "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",
//
//  "com.twitter" %% "finatra-http" % versions.finatra % "test" classifier "tests",
//  "com.twitter" %% "finatra-jackson" % versions.finatra % "test" classifier "tests",
//  "com.twitter" %% "inject-server" % versions.finatra % "test" classifier "tests",
//  "com.twitter" %% "inject-app" % versions.finatra % "test" classifier "tests",
//  "com.twitter" %% "inject-core" % versions.finatra % "test" classifier "tests",
//  "com.twitter" %% "inject-modules" % versions.finatra % "test" classifier "tests",
//
//  "org.mockito" % "mockito-core" % "1.9.5" % "test",
//  "org.scalatest" %% "scalatest" % "2.2.3" % "test",
//  "org.specs2" %% "specs2" % "2.3.12" % "test"
)

resolvers ++= Seq(
  "Twitter" at "http://maven.twttr.com",
  "spray repo" at "http://repo.spray.io",
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
)

//unmanagedBase :=  baseDirectory.value / "mylib"

mainClass in assembly := Some("unlimited_works.blog.spray.Main")