package com.wa9nnn.rotator.ui

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotator.arco.Headerlistener
import com.wa9nnn.rotator.{RotatorConfig, RotatorInterface}
import com.wa9nnn.rotator.rg.ResponseParser.Degree
import com.wa9nnn.rotator.rg.{RGHeader, Rotator}
import org.jfree.chart.entity.ChartEntity
import org.jfree.chart.plot.CompassPlot
import org.jfree.chart.{ChartMouseEvent, ChartMouseListener, ChartPanel, JFreeChart}
import org.jfree.data.general.DefaultValueDataset
import org.scalafx.extras.onFX
import scalafx.scene.control.Label
import scalafx.scene.layout.BorderPane
import scalafx.scene.text.Text

import java.awt.event.MouseEvent
import scala.language.implicitConversions

/**
 * A swing Panel that displays and interacts with an Antenna Genius rotator motor.
 */
class RotatorPanel(rotatorConfig: RotatorConfig, rotatorInterface: RotatorInterface) extends BorderPane with Headerlistener with LazyLogging with ChartMouseListener {
  private val compassDataSet = new DefaultValueDataset(1)
  styleClass += "rotatorPanel"
  private val nameLabel = new Text(rotatorConfig.name){
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

  val chartPanel: ChartPanel = new ChartPanel(compassChart)
  chartPanel.addChartMouseListener(this)


  import org.jfree.chart.fx.ChartViewer

  val viewer: ChartViewer = new ChartViewer(compassChart)
  //  stage.setScene(new Nothing(viewer))
  //  stage.setTitle("JFreeChart: Histogram")
  //  stage.setWidth(600)
  //  stage.setHeight(400)
  viewer.getStyleClass.add("compass")


  viewer.setPrefSize(200.0, 200.0)
  center.value = viewer
  bottom = azimuthDisplay

  rotatorInterface.addListener(this)

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

  override def newHeader(RGHeader: RGHeader): Unit = {

    onFX {
      rotatorInterface.getPosition.foreach { azimuth: Degree =>
        compassDataSet.setValue(Integer.valueOf(azimuth))
        azimuthDisplay.text = f"$azimuth%40d"
      }
    }
  }

  def chartMouseClicked(event: org.jfree.chart.ChartMouseEvent): Unit = {
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
    logger.trace(event.toString)
  }
}
