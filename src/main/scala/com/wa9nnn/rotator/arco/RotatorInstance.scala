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

package com.wa9nnn.rotator.arco

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotator.{Degree, RotatorConfig}
import com.wa9nnn.rotator.ui.RotatorPanel
import scalafx.beans.property.ObjectProperty

import java.util.UUID

/**
 * Holds everything we know about one Rotator as configured.
 */
case class RotatorInstance(rotatorConfig: RotatorConfig, selectedRouter: ObjectProperty[UUID], arcoConfig: Config) extends LazyLogging {
  val name: String = rotatorConfig.name

  val rotatorStateProperty: ObjectProperty[RotatorState] = ObjectProperty[RotatorState](RotatorState())


  private val arcoInterface: ArcoInterface = new ArcoInterface(rotatorConfig, arcoConfig, rotatorStateProperty)

  def move(targetAzimuth: Degree): Unit = {
    arcoInterface.move(targetAzimuth)
  }

  val rotatorPanel = new RotatorPanel(this)

  def stop(): Unit = {
    rotatorPanel.stop()
    arcoInterface.stop()
  }
}

