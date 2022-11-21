
import com.google.inject.{Guice, Injector}
import com.wa9nnn.rotator.arco.ArcoCoordinator
import com.wa9nnn.rotator.metrics.{MetricsReporter, Reporter}
import com.wa9nnn.rotator.ui.config.ConfigEditorDialog
import com.wa9nnn.rotator.{AppConfig, GuiceModule}
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem}
import scalafx.scene.layout.{BorderPane, HBox}



/**
 * Main
 * Handles command line, if all ok invoke $Server
 */
object RotatorControl extends JFXApp3 {

  private var injector: Injector = _


  private val editRotatorMenuItem = new MenuItem {
    text = "Config"
    onAction = { _ =>
      injector.instance[ConfigEditorDialog].showAndWait()
    }
  }
  private val metricsMenu = new MenuItem {
    text = "Metrics"
    onAction = { _ =>
      injector.instance[MetricsReporter].report()
    }
  }

  private val todoConfig: AppConfig = AppConfig()
  //  private val server = new Server(todoConfig)

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Rotator Manager"
      onCloseRequest = { e =>
        Platform.exit()
        System.exit(0)
      }
    }
    injector = Guice.createInjector(new GuiceModule())
    lazy val arcoCoordinator: ArcoCoordinator = injector.instance[ArcoCoordinator]


    val secne: Scene = new Scene {
      val cssUrl: String = getClass.getResource("/rotatormanager.css").toExternalForm
      stylesheets.add(cssUrl)

      private val menuBar = new MenuBar {
        useSystemMenuBar = true
      }
      val confgMenu = new Menu("Config") {
        items += editRotatorMenuItem
        items += metricsMenu
      }

      menuBar.menus = List(confgMenu)
      root = new BorderPane {

        top = menuBar
        center = new HBox {
          children = arcoCoordinator.rotatorPanels.iterator.toSeq
        }
        //          center = tabPane
        bottom = new Label("bottom")
        //          right = injector.instance[NetworkPane]
      }
    }
    stage.scene = secne
  }
}
