lazy val commonSettings = Seq(
  organization := "com.whisk",
  version := "0.9.9-C",
  scalaVersion := "2.13.0",
  crossScalaVersions := Seq("2.13.0", "2.12.8", "2.11.12"),
  scalacOptions ++= Seq("-feature", "-deprecation"),
  fork in Test := true,
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  sonatypeProfileName := "com.whisk",
  publishMavenStyle := true,
  publishTo := Some("Private Nexus" at System.getenv("DEPLOYMENT_REPOSITORY_URI")),
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  resolvers += Resolver.mavenLocal,
  pomExtra in Global := {
    <url>https://github.com/whisklabs/docker-it-scala</url>
      <scm>
        <connection>scm:git:github.com/whisklabs/docker-it-scala.git</connection>
        <developerConnection>scm:git:git@github.com:whisklabs/docker-it-scala.git</developerConnection>
        <url>github.com/whisklabs/docker-it-scala.git</url>
      </scm>
      <developers>
        <developer>
          <id>viktortnk</id>
          <name>Viktor Taranenko</name>
          <url>https://github.com/viktortnk</url>
        </developer>
        <developer>
          <id>alari</id>
          <name>Dmitry Kurinskiy</name>
          <url>https://github.com/alari</url>
        </developer>
      </developers>
  }
)

lazy val root =
  project
    .in(file("."))
    .settings(commonSettings: _*)
    .settings(publish := {}, publishLocal := {}, packagedArtifacts := Map.empty)
    .aggregate(core, testkitSpotifyImpl, testkitSpotifyShadedImpl, testkitDockerJavaImpl, config, scalatest, specs2, samples)

lazy val core =
  project
    .settings(commonSettings: _*)
    .settings(name := "docker-testkit-core",
              libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.22")

//<dependency>
//  <groupId>com.github.jnr</groupId>
//  <artifactId>jffi</artifactId>
//  <version>1.2.15</version>
//</dependency>
// "com.github.jnr" % "jffi" % "1.2.15"


lazy val testkitSpotifyImpl =
  project
    .in(file("impl/spotify"))
    .settings(commonSettings: _*)
    .settings(name := "docker-testkit-impl-spotify",
              libraryDependencies ++=
                Seq("com.spotify" % "docker-client" % "8.16.0-C",
                    "com.google.code.findbugs" % "jsr305" % "3.0.1",
                    "com.github.jnr" % "jffi" % "1.2.15",
                    "com.github.jnr" % "jffi" % "1.2.15" classifier "native"))
    .dependsOn(core)

lazy val testkitSpotifyShadedImpl =
  project
    .in(file("impl/spotify"))
    .settings(commonSettings: _*)
    .settings(name := "docker-testkit-impl-spotify-shaded",
              libraryDependencies ++=
                Seq("com.spotify" % "docker-client" % "8.16.0-C",
                    "com.google.code.findbugs" % "jsr305" % "3.0.1",
                    "com.github.jnr" % "jffi" % "1.2.15",
                    "com.github.jnr" % "jffi" % "1.2.15" classifier "native"),
              target := baseDirectory.value / "target-shaded"
              )
    .dependsOn(core)

lazy val testkitDockerJavaImpl =
  project
    .in(file("impl/docker-java"))
    .settings(commonSettings: _*)
    .settings(
      name := "docker-testkit-impl-docker-java",
      libraryDependencies ++=
        Seq("com.github.docker-java" % "docker-java" % "3.0.13",
            "com.google.code.findbugs" % "jsr305" % "3.0.1")
    )
    .dependsOn(core)

lazy val samples =
  project
    .settings(commonSettings: _*)
    .settings(name := "docker-testkit-samples")
    .dependsOn(core)

lazy val scalatest =
  project
    .settings(commonSettings: _*)
    .settings(
      name := "docker-testkit-scalatest",
      libraryDependencies ++=
        Seq("org.scalatest" %% "scalatest" % "3.0.8",
            "ch.qos.logback" % "logback-classic" % "1.2.1" % "test",
            "org.postgresql" % "postgresql" % "9.4.1210" % "test")
    )
    .dependsOn(core, testkitSpotifyImpl % "test", testkitDockerJavaImpl % "test", samples % "test")

lazy val specs2 =
  project
    .settings(commonSettings: _*)
    .settings(
      name := "docker-testkit-specs2",
      libraryDependencies ++=
        Seq("org.specs2" %% "specs2-core" % "4.5.1",
            "ch.qos.logback" % "logback-classic" % "1.2.1" % "test",
            "org.postgresql" % "postgresql" % "9.4.1210" % "test")
    )
    .dependsOn(core, samples % "test", testkitDockerJavaImpl % "test")

lazy val config =
  project
    .settings(commonSettings: _*)
    .settings(
      name := "docker-testkit-config",
      libraryDependencies ++=
        Seq(
          "com.iheart" %% "ficus" % "1.4.7",
          "org.scalatest" %% "scalatest" % "3.0.8" % "test"
        )
    )
    .dependsOn(core, testkitDockerJavaImpl)
