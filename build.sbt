import ByteConversions._
import com.typesafe.sbt.packager.docker._

lazy val commonSettings: Seq[Setting[_]] = Seq(
  version in ThisBuild := "2.0"
  )

lazy val root = (project in file(".")).
  enablePlugins(ConductRPlugin, JavaAppPackaging).
  settings(
    commonSettings,
    name := "docker-registry-v2",
    dockerCommands := Seq(
      Cmd("FROM", "registry:2"),
    ),
    scriptClasspathOrdering := Seq.empty,
    BundleKeys.system := "docker-registry-v2",
    BundleKeys.bundleType := Docker,
    BundleKeys.nrOfCpus := 1.0,
    BundleKeys.memory := 256.MiB,
    BundleKeys.diskSpace := 100.GB,
    BundleKeys.roles := Set("docker-registry"),
    BundleKeys.endpoints := Map(
      "registry" -> Endpoint("http", 5000, services = Set(uri("http://:5000")))
    ),
    BundleKeys.checks := Seq(uri("docker+$REGISTRY_HOST/v2/"))
  )
