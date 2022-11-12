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

import com.wa9nnn.rotator.RotatorConfig
import scalafx.scene.control.{ButtonType, Dialog, MenuItem}
import _root_.scalafx.Includes._
import com.wa9nnn.util.HostAndPort
import javafx.scene.control.DialogPane
import scalafx.beans.property.StringProperty
import scalafx.stage.Window

class RotatorEditorDialog(owner: Window, rotatorConfig: RotatorConfig) extends Dialog[RotatorConfig] {
  initOwner(owner)
  title = "Rotator Config"
  headerText = "Define a rotator"
  private val goc = new GridOfControls
  val nameProperty: StringProperty = goc.addText("Name", defValue = rotatorConfig.name,  tooltip = Some("Name of ARCO, nice if it matches Rotator Name in ARCO setup."))
  val hostProperty: StringProperty = goc.addText("Host",  defValue = rotatorConfig.hostAndPort.toString,  tooltip = Some("Host and port of the ARCO. e.g. 10.10.10.1:4001"))

  private val dp: DialogPane = dialogPane()
  dp.content = goc
  dp.buttonTypes = Seq(ButtonType.Close, ButtonType.OK)


  resultConverter = {
    case ButtonType.OK =>
      RotatorConfig(nameProperty.value, HostAndPort(hostProperty.value, 4001)); //todo
    case x =>
      null
  }
}


