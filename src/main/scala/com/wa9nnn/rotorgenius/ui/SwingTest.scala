package com.wa9nnn.rotorgenius.ui

import com.wa9nnn.rotorgenius.rg.ResponseParser.Degree
import com.wa9nnn.rotorgenius.rg.{Headerlistener, RGHeader, RotatorGeniusInterface}

import javax.swing.SpringLayout.Constraints
import javax.swing.SwingUtilities.invokeAndWait
import scala.swing.{GridPanel, Label, Table}


class SwingTest(rotatorGeniusInterface: RotatorGeniusInterface) extends Headerlistener {

  import scala.swing._

  rotatorGeniusInterface.addListener(this)
  val azimuth = new Label("42")

  val f = new MainFrame {
    title = "Hello world"
    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem("XYZZY") {
        }
      }
    }

    contents = new RgHeaderPanel()
    size = new Dimension(300, 300)
    centerOnScreen
  }
  f.visible = true
  /*
      rotatorGeniusInterface.currentHeader.foreach { rdheader =>
        rdheader.productIterator.foreach { element =>

          println(element)

        }
      }
  */


  var currentPosition: Degree = -1

  override def header(header: RGHeader): Unit =
    header.rotators(rotatorGeniusInterface.currentRotator).currentAzimuth.foreach {
      newAzimuth =>
        if (currentPosition != newAzimuth) {
          currentPosition = newAzimuth
          invokeAndWait(new Runnable {
            override def run(): Unit = {
              currentPosition = newAzimuth
              azimuth.text = currentPosition.toString
            }
          })
        }
    }

}

  class RgHeaderPanel extends GridPanel(2, 2) {
    private val constraints = new Constraints()
    contents += new Label("Hello")
    contents += new Label("123")
    contents += new Label("r2c0")
    contents += new Label("r2c2")
  }
