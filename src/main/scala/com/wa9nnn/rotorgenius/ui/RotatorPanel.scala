package com.wa9nnn.rotorgenius.ui

import com.wa9nnn.rotorgenius.rg.Moving._
import com.wa9nnn.rotorgenius.rg.ResponseParser.Degree
import com.wa9nnn.rotorgenius.rg.Rotator
import org.jfree.chart.{ChartPanel, JFreeChart}
import org.jfree.chart.plot.CompassPlot
import org.jfree.data.general.DefaultValueDataset

import javax.swing.SwingUtilities
import scala.language.implicitConversions
import scala.swing.BorderPanel.Position
import scala.swing.{BorderPanel, Component, Label}

class RotatorPanel() extends BorderPanel {
  private val compassDataSet = new DefaultValueDataset(1)

  private val nameLabel = new Label("?")
  add(nameLabel, Position.North)


  val compassPlot: CompassPlot = new CompassPlot(compassDataSet)
  compassPlot.setLabelType(CompassPlot.VALUE_LABELS)
  val compassChart: JFreeChart = new JFreeChart(
    compassPlot
  )
  val chartPanel: ChartPanel = new ChartPanel(compassChart)

  private val wrappedChart: Component = Component.wrap(chartPanel)
  add(wrappedChart, Position.Center)

  private val cureentAzimuthLabel = new Label("?")
  add(cureentAzimuthLabel, Position.South)

  def apply(rotator: Rotator): Unit = {
    implicit def degRender(maybeDegree: Option[Degree]): String = {
      maybeDegree match {
        case Some(value) =>
          s"$value\u00B0"
        case None =>
          "?"
      }
    }

    def showStopped(maybeDegree: Option[Degree]): String = {
      maybeDegree
    }

    def showMoving(rotator: Rotator): String = {
      val current: String = rotator.currentAzimuth
      val target: String = rotator.targetAzimuth
      s"$current ➜ $target"
    }

    SwingUtilities.invokeLater(() => {
      nameLabel.text = rotator.name
      rotator.currentAzimuth.foreach { azimuth =>
        compassDataSet.setValue(Integer.valueOf(azimuth))

        cureentAzimuthLabel.text = rotator.moving match {
          case Stopped =>
            showStopped(rotator.currentAzimuth)
          case CW | CCW =>
            showMoving(rotator)
        }
      }
    })
  }
}

