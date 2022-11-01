package com.wa9nnn.rotorgenius.ui

import org.jfree.chart.{ChartPanel, JFreeChart}
import org.jfree.chart.plot.CompassPlot
import org.jfree.data.general.DefaultValueDataset

import java.awt.{BasicStroke, Color, Paint, Stroke}
import scala.swing.Action.NoAction.title
import scala.swing.{BorderPanel, Component, Graphics2D}

class Compass extends BorderPanel {
  private val canvas = new Canvas()
  add(canvas, BorderPanel.Position.Center)


}

class Canvas() extends Component {

  override def paintComponent(g: Graphics2D): Unit = {
    val d = size
    g.setColor(Color.BLUE);
    g.fillRect(0, 0, d.width, d.height);

    g.setColor(Color.white)
    g.setStroke(new BasicStroke())
    g.drawOval(200, 200, 100, 100)



    //    val rowWid = d.height / blocks.height
    //    val colWid = d.width / blocks.width
    //    val wid = rowWid min colWid
    //    val x0 = (d.width - blocks.width * wid)/2
    //    val y0 = (d.height - blocks.height * wid)/2
    //    for (x <- 0 until blocks.width) {
    //      for (y <- 0 until blocks.height) {
    //        g.setColor(FloodIt.colorFor(blocks.get(Pos(x, y))))
    //        g.fillRect(x0 + x * wid, y0 + y * wid, wid, wid)
    //      }
    //    }
    //  }
  }
}