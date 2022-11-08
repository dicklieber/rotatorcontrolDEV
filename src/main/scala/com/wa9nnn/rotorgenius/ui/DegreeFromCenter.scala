package com.wa9nnn.rotorgenius.ui

import scala.swing.{Point, Rectangle}

/**
 * For a given [[Rectangle]] calculate the angle from a point at the center.
 *
 * @param rectangle typically of a [[org.jfree.chart.plot.CompassPlot]]
 */
case class DegreeFromCenter(rectangle: Rectangle) {
  private val center = new Point(rectangle.getCenterX.toInt, rectangle.getCenterY.toInt)

  /**
   *
   * @param point of interest tupically where the mouse is pointing.
   * @return
   */
  def apply(point: Point): Int = {
    val d = getAngleOfLineBetweenTwoPoints(center, point)
    d.toInt + 180
  }

  /**
   * From https://gist.githubusercontent.com/darzo27/d25161f13686633bd52162044b30b7db/raw/cf92f951e07089db7eb60a6adf017ea4d643a2f4/CalcAngleofLineBetweenTwoPoints
   * but swapped yDiff p1 and p2
   */
  private def getAngleOfLineBetweenTwoPoints(p1: Point, p2: Point): Double = {
    val xDiff = p2.x - p1.x
    val yDiff = p1.y - p2.y
    Math.toDegrees(Math.atan2(yDiff, xDiff))
  }
}
