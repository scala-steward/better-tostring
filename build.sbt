val commonSettings = Seq(
  scalaVersion := "2.12.12"
)

val plugin = project.settings(
  name := "better-tostring",
  commonSettings,
  crossTarget := target.value / s"scala-${scalaVersion.value}", // workaround for https://github.com/sbt/sbt/issues/5097
  crossVersion := CrossVersion.full,
  libraryDependencies ++= Seq(
    scalaOrganization.value % "scala-compiler" % scalaVersion.value
  )
)

val examples = project.settings(
  skip in publish := true,
  commonSettings,
  scalacOptions ++= {
    val jar = (plugin / Compile / packageBin).value
    Seq(
      s"-Xplugin:${jar.getAbsolutePath}",
      s"-Xplugin-require:better-tostring",
      s"-Jdummy=${jar.lastModified}"
    ) //borrowed from bm4
  }
)

val betterToString =
  project
    .in(file("."))
    .settings(name := "root")
    .settings(commonSettings, skip in publish := true)
    .dependsOn(plugin)
    .aggregate(plugin)
