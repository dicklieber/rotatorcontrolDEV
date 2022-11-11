package com.wa9nnn.rotator.ui

import com.wa9nnn.rotator.{Config, RotatorConfig}

import scala.swing.{BoxPanel, Dialog, GridPanel, Label, Orientation, TextField, Window}

class ConfigRotatorEditorPanel(config: RotatorConfig) extends GridPanel(2, 2) {
  contents += new Label("Name:")
  contents += new TextField("?", 30)
  contents += new Label("Host:")
  contents += new TextField("192.168.0.123:4001", 30)
}


class RotatorEditorDialog(config: RotatorConfig, owner: Window) extends Dialog(owner) {
  contents = new BoxPanel(Orientation.Vertical){
    contents +=  new ConfigRotatorEditorPanel(config)
    contents +=
  }
  open()
}

