
import Keys._

import sbtassembly.Plugin._

import AssemblyKeys._

name := "CleanWebHackathon2013"

version := "1.0"

scalaVersion := "2.9.3"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Spray Repository" at "http://repo.spray.cc/"

// libraryDependencies += "org.spark-project" %% "spark-core" % "0.7.2"

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

seq(assemblySettings: _*)

test in assembly := {}

