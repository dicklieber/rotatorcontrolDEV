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

package com.wa9nnn.rotator.ui

import com.wa9nnn.rotator.{BuildInfo, ConfigManager}
import com.wa9nnn.util.TimeConverters
import scalafx.Includes._
import scalafx.scene.control._

import java.awt.Desktop
import java.net.URI
import java.time.Instant
import javax.inject.Inject

class AboutDialog @Inject()(configManager: ConfigManager) extends Dialog[String] {
  implicit val desktop: Desktop = Desktop.getDesktop

  //  val fileSeperator: String = System.getProperty("file.separator")
  val pathSeperator: String = System.getProperty("path.separator")
  title = "About RotatorControl"
  headerText = "Manager Rotators"
  resizable = true

  private val dp: DialogPane = dialogPane()


  dp.buttonTypes = Seq(ButtonType.OK)

  private val goc = new GridOfControls(cellStyle = "aboutGridCell")

  def gocProperty(key: String): Unit = {
    goc.addLabel(key, System.getProperty(key))
  }

  def gocPath(key: String): Unit = {
    val str: String = System.getProperty(key)
      .split(pathSeperator)
      .mkString("\n")
    goc.addLabel(key, str)
  }

  private val builtAt = TimeConverters.instantDisplayUTCLocal(Instant.ofEpochMilli(BuildInfo.builtAtMillis))
  private val githublink: String = "https://github.com/dicklieber/rotatorcontrol"
  val cellStyle: String = "cellStyle"
  goc.addLabel("Version", BuildInfo.version)
  goc.addLabel("Built at", builtAt)
  goc.addLabel("Git Branch", BuildInfo.gitCurrentBranch)
  goc.addLabel("Config Location", configManager.defaultPath)
  goc.addControl("Source Code", new  Hyperlink(githublink) {
    onAction = event => {
      desktop.browse(new URI(githublink))
    }
  })

  gocProperty("file.separator")
  gocPath("java.class.path")
  gocProperty("java.home")
  gocProperty("java.vendor")
  gocProperty("os.arch")
  gocProperty("os.name")
  gocProperty("os.version")
  gocProperty("user.dir")
  gocProperty("user.home")
  gocProperty("user.language")
  gocProperty("user.name")


  goc.styleClass + "aboutGrid"


  dp.setContent(new ScrollPane {
    content = goc
    prefWidth = 440
    prefHeight = 300

  }
  )
  dp.prefWidth.value = (650)
  dp.prefHeight.value = (600)

}


