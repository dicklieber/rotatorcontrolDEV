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
import com.wa9nnn.rotator.Degree
import com.wa9nnn.rotator.arco.{ArcoOperation, RotatorInstance}
import javafx.geometry.Bounds
import javafx.scene.input.MouseEvent
import org.jfree.chart.JFreeChart
import org.jfree.chart.fx.interaction.{ChartMouseEventFX, ChartMouseListenerFX}
import org.jfree.chart.plot.CompassPlot
import org.jfree.data.general.DefaultValueDataset
import org.scalafx.extras.onFX
import scalafx.event.subscriptions.Subscription
import scalafx.geometry.{Point2D, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.{BorderPane, FlowPane, Pane, Priority, VBox}
import scalafx.scene.text.Text

import java.util.UUID
import javax.inject.Inject
import scala.language.{existentials, implicitConversions}

/**
 * A Panel that displays and interacts with an ARCO rotator .
 */
class RotatorPanel @Inject()(rotatorStuff: RotatorInstance) extends BorderPane with LazyLogging {
  val cssUrl: String = getClass.getResource("/rotatormanager.css").toExternalForm
  stylesheets.add(cssUrl)

  val ourId: UUID = rotatorStuff.rotatorConfig.id

  private val nameLabel = new Label("---") {
    styleClass += "rotatorName"
    onMouseClicked = e => {
      rotatorStuff.selectedRouter.value = ourId
    }
  }

  private val azimuthLabel: Text = new Text("---") {
    styleClass += "azimuthLabel"
  }

  val subscription: Subscription = rotatorStuff.onChange { (_, _, rotatorState) =>
    val azimuth: Degree = rotatorState.currentAzimuth
    onFX {
      compassDataSet.setValue(azimuth.degree)
      azimuthLabel.text = azimuth.toString
      nameLabel.text = rotatorState.name
    }
  }

  def stop(): Unit = subscription.cancel()


  vgrow = Priority.Always
  hgrow = Priority.Always
  styleClass += "rotatorPanel"
  private val compassDataSet = new DefaultValueDataset(1)
  rotatorStuff.selectedRouter.onChange { (_, _, is) =>
    if (ourId == is) {
      nameLabel.styleClass.addOne("selectedRouter")
      nameLabel.tooltip = "This is the rotator used with rotctld."
    } else {
      nameLabel.styleClass.subtractOne("selectedRouter")
      nameLabel.tooltip = "Click to use with rotctld."
    }
  }


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
      if (mouseEvent.getClickCount == 2) {
        val bounds: Bounds = jfxViewer.getCanvas.getBoundsInLocal
        val center: Point2D = new Point2D(bounds.getCenterX, bounds.getCenterY)
        val mousePoint: Point2D = new Point2D(mouseEvent.getX.toInt, mouseEvent.getY.toInt)
        val zeroPoint = new Point2D(bounds.getCenterX, 0)
        val angle = center.angle(zeroPoint, mousePoint)
        val finalA: Double = if (mousePoint.x < center.x)
          360 - angle
        else
          angle

        logger.debug(s"finalA: $finalA")
        rotatorStuff.move(Degree(finalA))
      }
    }

    override def chartMouseMoved(event: ChartMouseEventFX): Unit = {
      logger.trace("moved: {}", event.getTrigger)
    }
  })

  jfxViewer.setPrefSize(200.0, 200.0)
  //  top = nameLabel
  top = new FlowPane {
    children += nameLabel
    alignment = Pos.Center
    styleClass += "bigText"
  }
  center.value = jfxViewer
  bottom = new VBox(
    new FlowPane {
      children += azimuthLabel
      alignment = Pos.Center
      styleClass += "bigText"
    },
    //    new Label(s"Rate: ${
    //      ArcoTask.pollsPerSeconds
    //    }/ sec State: ${
    //      ArcoTask.lastFailure
    //    } since: ${
    //      ArcoTask.lastFailure.stamp
    //    }")

  )
}



