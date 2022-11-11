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


