
import com.google.inject.{Guice, Injector}
import com.wa9nnn.rotator.rotctld.RotctldServer
import com.wa9nnn.rotator.ui.config.ConfigEditorDialog
import com.wa9nnn.rotator.{AppConfig, ConfigManager, GuiceModule, RotatorConfig}
import com.wa9nnn.util.HostAndPort
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem}
import scalafx.scene.layout.BorderPane

/*
 *   Copyright (C) 2022  Dick Lieber, WA9NNN
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/** import _root_.scalafx.event.ActionEvent
 * Main
 * Handles command line, if all ok invoke $Server
 */
object RotatorControl extends JFXApp3 {

  private  var injector: Injector  = _


  private val editRotatorMenuItem = new MenuItem {
    text = "Config"
    onAction = { _ =>
      injector.instance[ConfigEditorDialog].showAndWait()
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
      scene = new Scene {
        val cssUrl: String = getClass.getResource("/rotatormanager.css").toExternalForm
        stylesheets.add(cssUrl)

        private val menuBar = new MenuBar {
          useSystemMenuBar = true
        }
        val confgMenu = new Menu("Config") {
          items += editRotatorMenuItem
        }

        menuBar.menus = List(confgMenu)
        root = new BorderPane {

          top = menuBar
          private val rotatorConfig: RotatorConfig = RotatorConfig("XYZZY", HostAndPort("192.168.0.123", 4001))
//          center = new RotatorPanel(rotatorConfig, server.rotatorInterface)
          //          center = tabPane
          bottom = new Label("bottom")
          //          right = injector.instance[NetworkPane]
        }
      }
    }
injector = Guice.createInjector(new GuiceModule())
    val rotctldServer = injector.instance[RotctldServer]
  }
}
