import sbt._, Keys._

// Small defs to use in *.sbt files
object buildUtils {

  /** Apply @settings on the build level */
  def commonSettings(settings: Def.Setting[_]*) =
    inScope(ThisScope.in(ThisBuild))(settings)

  def configure(config: Configuration)(settings: Def.Setting[_]*) =
    inConfig(config)(settings)

}
