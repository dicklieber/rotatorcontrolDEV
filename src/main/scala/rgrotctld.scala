
import com.wa9nnn.rotorgenius.{BuildInfo, CommandLine, Server}
import com.wa9nnn.rotorgenius.CommandLine.{defaultRotatorGeniusPort, defaultRotctldPort}
import com.wa9nnn.util.HostAndPort

/**
 * Main
 * Handles command line, if all ok invoke $Server
 */
object rgrotctld extends App {

  import scopt.OParser

  val builder = OParser.builder[CommandLine]
  val parser1 = {
    import builder._
    OParser.sequence(
      programName(BuildInfo.name),
      head(BuildInfo.name, BuildInfo.version),
      opt[String]('r', "rgHost")
        .validate(candidate =>
          try {
            HostAndPort(candidate, defaultRotatorGeniusPort)
            success
          } catch {
            case e: Exception =>
              failure(e.getMessage)
          }
        )
        .action((h, c) => c.copy(controllerHostAndPort = HostAndPort(h, defaultRotatorGeniusPort)))
        .required
        .text(s"Host and port of the Rotator Genius. e.g. 192.168.0.16:1234 port default: $defaultRotatorGeniusPort"),

      opt[Int]('p', "port")
        .action((p, c) => c.copy(rotctldPort = p))
        .text(s"default: $defaultRotctldPort"),

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