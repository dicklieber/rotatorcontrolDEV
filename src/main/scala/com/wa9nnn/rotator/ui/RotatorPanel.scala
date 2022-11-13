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

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotator.arco.Headerlistener
import com.wa9nnn.rotator.rg.RGHeader
import com.wa9nnn.rotator.rg.ResponseParser.Degree
import com.wa9nnn.rotator.{RotatorConfig, RotatorInterface}
import javafx.geometry.Bounds
import javafx.scene.input.MouseEvent
import org.jfree.chart.JFreeChart
import org.jfree.chart.fx.interaction.{ChartMouseEventFX, ChartMouseListenerFX}
import org.jfree.chart.plot.CompassPlot
import org.jfree.data.general.DefaultValueDataset
import org.scalafx.extras.onFX
import scalafx.geometry.Point2D
import scalafx.scene.control.Label
import scalafx.scene.layout.BorderPane
import scalafx.scene.text.Text

import scala.language.implicitConversions

/**
 * A swing Panel that displays and interacts with an Antenna Genius rotator motor.
 */
class RotatorPanel(rotatorConfig: RotatorConfig, rotatorInterface: RotatorInterface) extends BorderPane with Headerlistener with LazyLogging {
  private val compassDataSet = new DefaultValueDataset(1)
  styleClass += "rotatorPanel"
  private val nameLabel = new Text(rotatorConfig.name) {
    styleClass += "rotatorName"
  }
  private val azimuthDisplay: Text = new Text("---") {
    styleClass += "azimuthDisplay"
  }
  top = nameLabel
  bottom = new Label("Bottom of rotator panel")

  val compassPlot: CompassPlot = new CompassPlot(compassDataSet)

  compassPlot.setLabelType(CompassPlot.VALUE_LABELS)
  val compassChart: JFreeChart = new JFreeChart(
    compassPlot
  )


  import org.jfree.chart.fx.ChartViewer

  val jfxViewer: ChartViewer = new ChartViewer(compassChart)
  jfxViewer.getStyleClass.add("compass")
  jfxViewer.addChartMouseListener(new ChartMouseListenerFX {
    override def chartMouseClicked(event: ChartMouseEventFX): Unit = {

      val mouseEvent: MouseEvent = event.getTrigger

      val bounds: Bounds = jfxViewer.getCanvas.getBoundsInLocal
      val center: Point2D = new Point2D(bounds.getCenterX, bounds.getCenterY)
      val mousePoint: Point2D = new Point2D(mouseEvent.getX.toInt, mouseEvent.getY.toInt)
      val zeroPoint = new Point2D(bounds.getCenterX, 0)
      val angle = center.angle(zeroPoint, mousePoint)
      val finalA = if (mousePoint.x < center.x)
        360 - angle
      else
        angle

      logger.debug(s"finalA: $finalA")
    }//todo use this to move

    override def chartMouseMoved(event: ChartMouseEventFX): Unit = {
      logger.trace("moved: {}", event.getTrigger)
    }
  })

  jfxViewer.setPrefSize(200.0, 200.0)

  center.value = jfxViewer
  bottom = azimuthDisplay

  rotatorInterface.addListener(this)

  /*  implicit def degRender(maybeDegree:

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
    }*/

  override def newHeader(RGHeader: RGHeader): Unit = {

    onFX {
      rotatorInterface.getPosition.foreach {
        azimuth: Degree =>
          compassDataSet.setValue(Integer.valueOf(azimuth))
          azimuthDisplay.text = f"$azimuth%40d"
      }
    }
  }


}


