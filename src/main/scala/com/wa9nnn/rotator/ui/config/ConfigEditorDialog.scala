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

package com.wa9nnn.rotator.ui.config

import com.wa9nnn.rotator.arco.ArcoManager
import com.wa9nnn.rotator.ui.config.ConfigEditorDialog.deleteImage
import com.wa9nnn.rotator.{AppConfig, ConfigManager, RotatorConfig}
import org.scalafx.extras.generic_dialog.NumberTextField
import scalafx.Includes._
import scalafx.beans.property.{IntegerProperty, StringProperty}
import scalafx.geometry.Pos
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.GridPane

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class ConfigEditorDialog @Inject()(configManager: ConfigManager) extends Dialog[AppConfig] {
  //class AboutDialog(owner: Window, appConfig: AppConfig) extends Dialog[AppConfig] {
  //  initOwner(owner)
  title = "Rotators"
  headerText = "Manager Rotators"
  resizable = true

  private val dp: DialogPane = dialogPane()


  dp.buttonTypes = Seq(ButtonType.Close, ButtonType.OK)

  resultConverter = {
    case ButtonType.OK =>
      val updated: AppConfig = currentAppConfig.collect
      configManager.save( updated)
      updated
    case _ =>
      null
  }

  buildGridPane(configManager.value) // initial.

  var currentAppConfig: AppConfig = _

  import scalafx.scene.Node

  def buildGridPane(appConfig: AppConfig): Unit = {
    currentAppConfig = appConfig
    val gridPane = new GridPane()

    def add(propertyField: StringProperty, row: Int, column: Int): Node = {
      val textField = new TextField()
      textField.text <==> propertyField
      gridPane.add(textField, column, row)
      textField
    }

    def addIntField(ipf: IntegerProperty, row: Int, column: Int): Unit = {
      val numberTextField = new NumberTextField(0)
      numberTextField.model.value <==> ipf
      gridPane.add(numberTextField, column, row)
    }

    val row = new AtomicInteger()
    gridPane.add(new Label("Name"), 0, 0)
    gridPane.add(new Label("Host"), 1, 0)
    gridPane.add(new Label("Port"), 2, 0)

    appConfig.rotators.foreach { rotatorConfig: RotatorConfig =>
      val r = row.incrementAndGet()
      add(rotatorConfig.nameProperty, r, 0).requestFocus()
      add(rotatorConfig.hostProperty, r, 1)
      addIntField(rotatorConfig.portProperty, r, 2)
      val graphic = new ImageView {
        image = deleteImage
        onMouseClicked = _ => {
          val appConfig1 = currentAppConfig.remove(rotatorConfig.id)
          buildGridPane(appConfig1)
        }
      }
      gridPane.add(graphic, 3, r)
    }

    gridPane.add(new Button {
      text = "Add Rotator"
      onAction = a => {
        buildGridPane(currentAppConfig.addRotator())

      }
    }, 2, row.incrementAndGet()
    )

    val rotctldPortRow = row.incrementAndGet()
    gridPane.add(new Label {
      text = "rotctld Port:"
      alignmentInParent = Pos.CenterRight
    }, 1, rotctldPortRow)

    val rf = new NumberTextField(0) {
      tooltip = "Will listen on this port for rotctld clients."
    }
    gridPane.add(rf, 2, rotctldPortRow)
    rf.model.value <==> appConfig.rotctldPortProperty
    dp.setContent(gridPane)

  }
}

object ConfigEditorDialog {
  val deleteImage = new Image("images/cancel-20.png")
}

