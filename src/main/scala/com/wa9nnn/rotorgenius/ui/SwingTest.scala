package com.wa9nnn.rotorgenius.ui

import com.wa9nnn.rotorgenius.rg.ResponseParser.Degree
import com.wa9nnn.rotorgenius.rg.{Headerlistener, RGHeader, Rotator, RotatorGeniusInterface}

import javax.swing.SwingUtilities
import scala.swing._


class SwingTest(rotatorGeniusInterface: RotatorGeniusInterface) extends Headerlistener {

  rotatorGeniusInterface.addListener(this)
  val azimuth = new Label("42")
  private val rotatorA: Table = new Table(new Rotator())

  private val rotatorB: Table = new Table(new Rotator())

//  private val column: TableColumn = rotatorB.peer.getColumnModel.getColumn(0)
//  column.setWidth(200)
//  column.setPreferredWidth(250)
  private val f: MainFrame = new MainFrame {
    title = "Rotator Genius"
    menuBar = new MenuBar {
      contents += new Menu("Debug") {
        contents += new MenuItem(Action("Raw Data") {
          println("Action '"+ title +"' invoked")
          val dialog: Dialog = new Dialog(f){
            size = new Dimension(500, 400)
            title = "Raw Data from Rotator Controller"
            modal = false
            val boxPanel = new BoxPanel(Orientation.Horizontal) {
              contents += rotatorA
              contents += rotatorB
            }
            contents = boxPanel
//            pack()
          }
          dialog.open()
        })
      }
    }

    private val boxPanel = new BoxPanel(Orientation.Horizontal) {
      contents += rotatorA
      contents += rotatorB
    }
    contents = boxPanel
    size = new Dimension(600, 300)
    centerOnScreen
  }
  f.visible = true

  var currentPosition: Degree = -1

  override def newHeader(header: RGHeader): Unit = {
    //noinspection ZeroIndexToHead
    SwingUtilities.invokeLater(() => {
      rotatorA.model = header.rotators(0)
      rotatorB.model = header.rotators(1)
    })
  }
}


