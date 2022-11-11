
import com.wa9nnn.rotator.{BuildInfo, CommandLine, Server}
import com.wa9nnn.rotator.CommandLine.{defaultRotatorGeniusPort, defaultRotctldPort}
import com.wa9nnn.util.HostAndPort

/**
 * Main
 * Handles command line, if all ok invoke $Server
 */
object RotatorManager extends App {

  import scopt.OParser

  val builder = OParser.builder[CommandLine]
  val parser1 = {
    import builder._
    OParser.sequence(
      programName(BuildInfo.name),
      head(BuildInfo.name, BuildInfo.version),

      opt[Unit]('v', "verbose")
        .action((_, c) => c.copy(verbose = true))
        .text("verbose is a flag")
      ,

      opt[Unit]('d', "debug")
        .action((_, c) => c.copy(debug = true))
        .text("this option is hidden in the usage text"),

      help("help").text("prints this usage text")
      ,
    )
  }
  OParser.parse(parser1, args, CommandLine()) match {
    case Some(config) =>
      new Server(config)
    case _ =>
    // arguments are bad, error message will have been displayed
  }

}