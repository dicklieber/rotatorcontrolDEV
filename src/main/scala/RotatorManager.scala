
import com.wa9nnn.rotator.ui.{RotatorEditorDialog, RotatorPanel}
import com.wa9nnn.rotator.{Config, RotatorConfig, Server}
import com.wa9nnn.util.HostAndPort
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem}
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color

/** import _root_.scalafx.event.ActionEvent
 * Main
 * Handles command line, if all ok invoke $Server
 */
object RotatorManager extends JFXApp3 {

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
        val cssUrl: String = getClass.getResource("/rotatormanager.css").toExternalForm
        stylesheets.add( cssUrl)

        private val menuBar = new MenuBar {
          useSystemMenuBar = true
        }
        val confgMenu = new Menu("Config") {
          items += editRotatorMenuItem
        }

        menuBar.menus = List(confgMenu)
        content = new BorderPane {

          top = menuBar
          private val rotatorConfig: RotatorConfig = RotatorConfig("XYZZY", HostAndPort("192.168.0.123", 4001))
          center = new RotatorPanel(rotatorConfig, server.rotatorInterface)
          //          center = tabPane
                  bottom = new Label("bottom")
          //          right = injector.instance[NetworkPane]
        }
      }
    }
  }
}
