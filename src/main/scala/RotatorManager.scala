
import com.wa9nnn.rotator.{Config, Server}
import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color.{DarkGray, DarkRed, Red, White}
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.text.Text

/**
 * Main
 * Handles command line, if all ok invoke $Server
 */
object RotatorManager extends JFXApp3 {

  //  val builder = OParser.builder[CommandLine]
  /*
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
  */
  //  OParser.parse(parser1, args, CommandLine()) match {
  //    case Some(config) =>
  //      new Server(config)
  //    case _ =>
  //    // arguments are bad, error message will have been displayed
  //  }

  private val todoConfig: Config = Config()
  private val server = new Server(todoConfig)
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Rotator Manager"
      scene = new Scene {
        fill = Color.rgb(38, 38, 38)
        content = new HBox {
          padding = Insets(50, 80, 50, 80)
          children = Seq(
            new Text {
              text = "Scala"
              style = "-fx-font: normal bold 100pt sans-serif"
              fill = new LinearGradient(
                endX = 0,
                stops = Stops(Red, DarkRed))
            },
            new Text {
              text = "FX"
              style = "-fx-font: italic bold 100pt sans-serif"
              fill = new LinearGradient(
                endX = 0,
                stops = Stops(White, DarkGray)
              )
              effect = new DropShadow {
                color = DarkGray
                radius = 15
                spread = 0.25
              }
            }
          )
        }
      }
    }
  }
}
