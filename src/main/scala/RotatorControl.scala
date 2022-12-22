
import com.google.inject.{Guice, Injector}
import com.wa9nnn.rotator.arco.ArcoManager
import com.wa9nnn.rotator.metrics.MetricsReporter
import com.wa9nnn.rotator.ui.AboutDialog
import com.wa9nnn.rotator.ui.config.ConfigEditorDialog
import com.wa9nnn.rotator.{BuildInfo, GuiceModule}
import com.wa9nnn.util.TimeConverters
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem}
import scalafx.scene.layout.BorderPane

import java.time.Instant

/**
 * Main
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
  private val aboutDialogItem = new MenuItem {
    text = "About"
    onAction = { _ =>
      injector.instance[AboutDialog].showAndWait()
    }
  }

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Rotator Manager"
      onCloseRequest = { _ =>
        Platform.exit()
        System.exit(0)
      }
    }
    injector = Guice.createInjector(new GuiceModule())
     val arcoManager: ArcoManager = injector.instance[ArcoManager]


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
     val aboutMenu = new Menu("Help") {
        items += aboutDialogItem
      }

      menuBar.menus = List(confgMenu, aboutMenu)
      root = new BorderPane {

        top = menuBar
        center = arcoManager
        //          center = tabPane

        val builtAt = TimeConverters.instantDisplayUTCLocal(Instant.ofEpochMilli(BuildInfo.builtAtMillis))
        bottom = new Label{
          text = s"Version ${BuildInfo.version} Branch: ${BuildInfo.gitCurrentBranch} Built At: $builtAt"
        }
        //          right = injector.instance[NetworkPane]
      }
    }
    stage.scene = secne
  }
}

