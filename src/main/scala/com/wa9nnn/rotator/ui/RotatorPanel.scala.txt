package com.wa9nnn.rotator.ui

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotator.rg.Moving._
import com.wa9nnn.rotator.rg.ResponseParser.Degree
import com.wa9nnn.rotator.rg.Rotator
import org.jfree.chart.entity.ChartEntity
import org.jfree.chart.{ChartMouseEvent, ChartMouseListener, ChartPanel, JFreeChart}
import org.jfree.chart.plot.CompassPlot
import org.jfree.data.general.DefaultValueDataset

import java.awt.event.MouseEvent
import javax.swing.SwingUtilities
import scala.language.implicitConversions
import scala.swing.BorderPanel.Position
import scala.swing.{BorderPanel, Component, Dimension, Label}

/**
 * A swing Panel that displays and interacts with an Antenna Genius rotator motor.
 */
class RotatorPanel() extends BorderPanel  with LazyLogging  with ChartMouseListener{
  private val compassDataSet = new DefaultValueDataset(1)

  private val nameLabel = new Label("?")
  add(nameLabel, Position.North)


  val compassPlot: CompassPlot = new CompassPlot(compassDataSet)
  compassPlot.setLabelType(CompassPlot.VALUE_LABELS)
  val compassChart: JFreeChart = new JFreeChart(
    compassPlot
  )


  val chartPanel: ChartPanel = new ChartPanel(compassChart)
  chartPanel.addChartMouseListener(this)
//  chartPanel.addChartMouseListener(this)
  private val wrappedChart: Component = Component.wrap(chartPanel)
  add(wrappedChart, Position.Center)
  private val c: Component = Component.wrap(chartPanel)
  wrappedChart.listenTo(mouse.clicks)
  wrappedChart.reactions += {
    case event:MouseEvent =>
      println(s"Event from: ${event.getComponent}")
      println(event)
  }

  private val size1: Dimension = wrappedChart.size

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

  override def chartMouseClicked(event: ChartMouseEvent): Unit = {
    logger.trace(event.toString)
    val chart: JFreeChart = event.getChart
    val trigger: MouseEvent = event.getTrigger
    val x = trigger.getX
    val y = trigger.getY
    val point = trigger.getPoint
    val str = trigger.paramString()

    val chartEntity: ChartEntity = event.getEntity
    val shapeType = chartEntity.getShapeType
    val area = chartEntity.getArea
    val bounds1 = area.getBounds

    val degreeFromCenter = DegreeFromCenter(bounds1)
    val angle = degreeFromCenter(point)

    logger.trace(s"click: point:$point  in: $bounds1 angle: $angle")

  }

  override def chartMouseMoved(event: ChartMouseEvent): Unit = {
//    logger.debug(event.toString)
  }

}

