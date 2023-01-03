/*
 * Copyright (C) 2023  Dick Lieber, WA9NNN
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.wa9nnn.rotator

import com.google.inject.{Guice, Injector}
import com.wa9nnn.rotator.arco.ArcoManager
import com.wa9nnn.rotator.metrics.MetricsReporter
import com.wa9nnn.rotator.ui.AboutDialog
import com.wa9nnn.rotator.ui.config.ConfigEditorDialog
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem}
import scalafx.scene.layout.BorderPane

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
    val imagePath: String = s"/images/docIcon.png"

    import java.awt.Taskbar
    val taskbar: Taskbar = Taskbar.getTaskbar
    import java.awt.{Image, Toolkit}

    val image: Image = Toolkit.getDefaultToolkit.getImage(getClass.getResource(imagePath))
    taskbar.setIconImage(image)


    val scene: Scene = new Scene {
      val cssUrl: String = getClass.getResource("/rotatormanager.css").toExternalForm
      stylesheets.add(cssUrl)

      private val menuBar = new MenuBar {
        useSystemMenuBar = true
      }
      val configMenu = new Menu("Config") {
        items += editRotatorMenuItem
        items += metricsMenu
      }
      val aboutMenu = new Menu("Help") {
        items += aboutDialogItem
      }

      menuBar.menus = List(configMenu, aboutMenu)
      root = new BorderPane {

        top = menuBar
        center = arcoManager
        //          center = tabPane

        bottom = new Label {
          text = s"Version ${BuildInfo.version}"
        }
      }
    }
    stage.scene = scene
  }
}
