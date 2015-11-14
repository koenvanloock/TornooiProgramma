name := """Tornooiprogramma"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test
)

javaOptions in Test += "-Dconfig.resource=test.conf"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.11.2.play24"

libraryDependencies +=  "mysql" % "mysql-connector-java" % "5.1.31"

libraryDependencies += "com.typesafe.play" %% "anorm" % "2.4.0"

libraryDependencies += "com.jason-goodwin" %% "authentikat-jwt" % "0.4.1"

libraryDependencies += "org.julienrf" %% "play-json-variants" % "2.0"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

coverageMinimum := 90

coverageFailOnMinimum := true

coverageHighlighting := true

coverageExcludedPackages := "<empty>;Reverse.*;router.*"
