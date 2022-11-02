package com.wa9nnn.rotorgenius.ui

import com.wa9nnn.rotorgenius.rg.Rotator
import org.jfree.chart.{ChartPanel, JFreeChart}
import org.jfree.chart.plot.CompassPlot
import org.jfree.data.general.DefaultValueDataset

import scala.swing.BorderPanel.Position
import scala.swing.{BorderPanel, Component, Label}

class RotatorPanel() extends BorderPanel {
  private val compassDataSet = new DefaultValueDataset(1)

  private val nameLabel = new Label("?")
  add(nameLabel, Position.North)

  val compassPlot: CompassPlot = new CompassPlot(compassDataSet)
  val compassChart: JFreeChart = new JFreeChart(
    compassPlot
  )
  val chartPanel: ChartPanel = new ChartPanel(compassChart)

  private val wrappedChart: Component = Component.wrap(chartPanel)
  add(wrappedChart, Position.Center)



  def apply(rotator: Rotator):Unit = {
    nameLabel.text = rotator.name
    rotator.currentAzimuth.foreach(azimuth=>
      compassDataSet.setValue(Integer.valueOf(azimuth))
    )
  }
}

