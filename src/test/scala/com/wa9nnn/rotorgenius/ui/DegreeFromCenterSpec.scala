package com.wa9nnn.rotorgenius.ui

import org.specs2.matcher.DataTables
import org.specs2.mutable.Specification
import org.specs2.specification.Tables

import scala.swing.{Point, Rectangle}


class DegreeFromCenterSpec extends Specification with DataTables {
  val rectangle: Rectangle = new Rectangle(100, 100)
  private val angleHelper = new DegreeFromCenter(rectangle)
  val north = new Point(0, 50)
  val south = new Point(100, 50)
  val east = new Point(50, 100)
  val west = new Point(50, 0)

  "various mouse positions" >> {
    "MousePoint" | "Result" |
      north !! 360 |
      south !! 180 |
      east !! 90 |
      west !! 270 |> { (p: Point, r: Int) =>
      val a = angleHelper(p)
      a must beEqualTo(r)
    }
  }
}

