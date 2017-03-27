organization    := "xtz"

name            := "tqs-stralet"

version         := "1.0-SNAPSHOT"

scalaVersion    := "2.11.8"

//resolvers       += "Local Maven Repository" at "file:///D:/java/maven/repo"
//resolvers       += "Scalaz Bintray Repo"    at "http://dl.bintray.com/scalaz/releases"


javacOptions   ++= Seq("-encoding", "UTF-8")


//libraryDependencies += "ch.qos.logback" % "logback-core" % "1.1.7"
//libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"
//libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.20"


libraryDependencies += "org.zeromq"          % "jeromq"        % "0.3.5"


libraryDependencies += "com.fasterxml.jackson.core"    % "jackson-core"         % "2.7.3"
libraryDependencies += "com.fasterxml.jackson.core"    % "jackson-annotations"  % "2.7.3"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.7.3"

libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.7.3"

libraryDependencies += "com.tictactec" % "ta-lib" % "0.4.0"




packAutoSettings

//packMain        := Map("tquant-api" -> "tquant.api.Boot")
//
//packJvmOpts     := Map("tquant-api" -> Seq(
//  "-Djava.library.path=${PROG_HOME}/lib"
//))
//
//packGenerateWindowsBatFile := true
