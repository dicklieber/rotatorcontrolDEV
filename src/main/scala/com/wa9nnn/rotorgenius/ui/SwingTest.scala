package com.wa9nnn.rotorgenius.ui

import com.wa9nnn.rotorgenius.rg.ResponseParser.Degree
import com.wa9nnn.rotorgenius.rg.{Headerlistener, RGHeader, RotatorGeniusInterface}

import javax.swing.SwingUtilities

class SwingTest(rotatorGeniusInterface: RotatorGeniusInterface) extends Headerlistener{

  import scala.swing._

  rotatorGeniusInterface.addListener(this)
  val azimuth = new Label("42")

  new Frame {
    title = "Hello world"

    rotatorGeniusInterface.currentHeader.foreach { rdheader =>
      rdheader.productIterator.foreach { element =>

        println(element)

      }
    }

    contents = new FlowPanel {
      contents += new Label("Azimuth:")
      contents += azimuth
      contents += new Label("Launch rainbows:")
      contents += new Button("Click me") {
        reactions += {
          case event.ButtonClicked(_) =>
            println("All the colours!")
        }
      }
    }

    pack()
    centerOnScreen()
    open()
  }

  var currentPosition:Degree = -1
  override def header(header: RGHeader): Unit =
    header.rotator2.currentAzimuth.foreach{ newAzimuth =>
      if( currentPosition != newAzimuth){
        currentPosition = newAzimuth
        SwingUtilities.invokeAndWait(new Runnable {
          override def run(): Unit = {
           currentPosition =  newAzimuth
            azimuth.text = currentPosition.toString
          }
        })
      }
    }

}
