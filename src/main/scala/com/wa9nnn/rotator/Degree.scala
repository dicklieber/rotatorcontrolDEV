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

package com.wa9nnn.rotator

/**
 *
 * @param degree -1 if unknown
 */
case class Degree(degree: Int = -1) {
  def threeDigits:String = {
    if (degree == -1)
      throw new IllegalArgumentException("Must have a value!")
    else
      f"$degree%03d"

  }

  assert(degree == -1 || degree >= 0 && degree <= 360, "Degree must be 0-360 or -1")

  override def toString: String =
    if (degree == -1)
      "?"
    else
      f"$degree%03d°"
}

object Degree {

  def apply(angle: Double): Degree = {
    new Degree(angle.toInt)
  }

  def apply(s: String): Degree = {
    s.toDouble.toInt match {
      case 999 =>
        new Degree()
      case d =>
        new Degree(d)
    }
  }
}


