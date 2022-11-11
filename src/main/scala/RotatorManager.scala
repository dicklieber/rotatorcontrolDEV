
import com.wa9nnn.rotator.{Config, RotatorConfig, Server}
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.{ButtonType, DConvert, Dialog, Menu, MenuBar, MenuItem}
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color
import javafx.{event => jfxe, scene => jfxs}
import _root_.scalafx.Includes._
import com.wa9nnn.rotator.ui.RotatorEditorDialog
import scalafx.event.ActionEvent
import scalafx.scene.control.ButtonBar.ButtonData

/** import _root_.scalafx.event.ActionEvent
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

  private val editRotatorMenuItem = new MenuItem {
    text = "Rotator Controller"
    onAction = {ae =>
       val maybeS = new RotatorEditorDialog(stage, RotatorConfig()).showAndWait()
      println(maybeS)
    }
  }

  private val todoConfig: Config = Config()
  private val server = new Server(todoConfig)

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Rotator Manager"
      onCloseRequest = { e =>
        Platform.exit()
        System.exit(0)
      }
      scene = new Scene {

        fill = Color.rgb(38, 38, 38)

        private val menuBar = new MenuBar {
          useSystemMenuBar = true
        }
        val confgMenu = new Menu("Config") {
          items += editRotatorMenuItem
        }


        menuBar.menus = List(confgMenu)
        content = new BorderPane {
          top = menuBar
          //          center = tabPane
          //          bottom = bottomPane
          //          right = injector.instance[NetworkPane]
        }
      }
    }
  }
}
