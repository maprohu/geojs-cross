import java.util.jar.Attributes

val geojsVersion = "0.1.0"
val githubRepo = "geojs-cross"
val osgiVersion = "5.0.0"

lazy val commonSettings = Seq(
  organization := "com.github.maprohu",
  version := geojsVersion,
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some(sbtglobal.SbtGlobals.devops)
//      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  credentials += sbtglobal.SbtGlobals.devopsCredentials,
  pomIncludeRepository := { _ => false },
  licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php")),
  homepage := Some(url(s"https://github.com/maprohu/${githubRepo}")),
  pomExtra := (
    <scm>
      <url>git@github.com:maprohu/{githubRepo}.git</url>
      <connection>scm:git:git@github.com:maprohu/{githubRepo}.git</connection>
    </scm>
      <developers>
        <developer>
          <id>maprohu</id>
          <name>maprohu</name>
          <url>https://github.com/maprohu</url>
        </developer>
      </developers>
    ),

  scalaVersion := "2.11.7"
)

lazy val commonOsgiSettings = Seq(
  OsgiKeys.additionalHeaders ++= Map(
    "-noee" -> "true",
    Attributes.Name.IMPLEMENTATION_VERSION.toString -> version.value
  ),
  publishArtifact in packageDoc := false,
  OsgiKeys.exportPackage := Seq(organization.value + "." + name.value.replaceAll("-", ".")),
  OsgiKeys.privatePackage := OsgiKeys.exportPackage.value.map(_ + ".impl"),
  OsgiKeys.bundleActivator := Some(OsgiKeys.privatePackage.value(0) + ".Activator"),
  libraryDependencies ++= Seq(
    "org.osgi" % "org.osgi.core" % osgiVersion % Provided
  )
)


lazy val geojsCross = (crossProject in file("."))
  .enablePlugins()
  .settings(
    (commonSettings ++ Seq(
      name := "geojs"
    )):_*
  )
  .jvmSettings(
    (commonOsgiSettings ++ osgiSettings):_*
  )


lazy val geojsCrossJS = geojsCross.js
lazy val geojsCrossJVM = geojsCross.jvm
  .enablePlugins(SbtOsgi)

lazy val root = (project in file("."))
  .aggregate(
    geojsCrossJS,
    geojsCrossJVM
  )
  .settings(
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))
  )
