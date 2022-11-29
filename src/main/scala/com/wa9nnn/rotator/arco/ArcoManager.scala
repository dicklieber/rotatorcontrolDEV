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
import com.wa9nnn.rotator.ui.RotatorPanel
import com.wa9nnn.rotator.{AppConfig, ConfigManager, Degree, RotatorConfig}
import scalafx.beans.property.ObjectProperty

import java.util.UUID
import javax.inject.Inject
import scala.collection.concurrent.TrieMap

/**
 * Manages instances of [[RotatorInstance]] based on changes in [[AppConfig]]
 *
 * @param configManager where to get current or changed configuration.
 */
class ArcoManager @Inject()(configManager: ConfigManager, config: Config) {
  private val arcoConfig = config.getConfig("arco")
  def selectedRotatorAzimuth: Degree = {
    val r: Option[Degree] = for {
      id <- Option(selectedRotator.value)
      rs <- rotatorMap.get(id)
    } yield {
      rs.value.currentAzimuth
    }
    r.getOrElse(Degree())
  }


  val rotatorMap = new TrieMap[UUID, RotatorInstance]()

  val selectedRotator: ObjectProperty[UUID] =  new ObjectProperty[UUID]()

  def updateRouterState(rotatorState: RotatorState): Unit = {
    val state: RotatorInstance = rotatorMap(rotatorState.id)
    state.value = rotatorState
  }

  def rotatorPanels: IterableOnce[RotatorPanel] = {
    rotatorMap.values.map { rotatusStuff: RotatorInstance =>
      rotatusStuff.rotatorPanel
    }
  }

  /**
   * Move the selected rotator
   *
   * @param targetAzimuth where to point to.
   */
  def moveSelected(targetAzimuth: Degree): Unit = {
    for {
      id <- Option(selectedRotator.value)
      rs <- rotatorMap.get(id)
    } {
      rs.move(targetAzimuth)
    }
  }

  configManager.onChange {
    (_, _, is: AppConfig) =>
      setup(is.rotators)
  }
  setup(configManager.value.rotators)

  def setup(rotators: Iterable[RotatorConfig]): Unit = {
    rotatorMap.values.foreach(_.stop())
    rotatorMap.clear()
    rotators.foreach {
      rc =>
        rotatorMap.put(rc.id, RotatorInstance(rc, selectedRotator, arcoConfig))
    }
    rotators.headOption.foreach{ rc=>
      selectedRotator.value = rc.id
    }
  }
}

/**
 * Holds everything we know about one Rotator as configured.
 */
case class RotatorInstance(rotatorConfig: RotatorConfig, selectedRouter: ObjectProperty[UUID], arcoConfig:Config) extends ObjectProperty[RotatorState]() {
  private val arcoInterface: ArcoInterface = new ArcoInterface(rotatorConfig, arcoConfig, this)


  def move(targetAzimuth: Degree): Unit = {
    arcoInterface.move(targetAzimuth)
  }

  val rotatorPanel = new RotatorPanel(this)

  def stop(): Unit = {
    rotatorPanel.stop()
    arcoInterface.stop()
  }
}

