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
import scalafx.collections.ObservableBuffer

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


  val rotatorMap = new TrieMap[UUID, RotatorInstance]()

  private val selectedRotator: ObjectProperty[UUID] = new ObjectProperty[UUID]()

  def selectedRotatorAzimuth: Degree = {

    selectedRotator.value match {
      case null =>
        throw new IllegalStateException("No selected router!")
      case id =>
        rotatorMap(id).rotatorStateProperty.value.currentAzimuth
    }
  }

  val rotatorPanels: ObservableBuffer[RotatorPanel] = ObservableBuffer[RotatorPanel]()

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
    rotatorPanels.clear()
    rotatorMap.values.foreach(_.stop())
    rotatorMap.clear()
    rotators.foreach {
      rc =>
        val rotatorInstance = RotatorInstance(rc, selectedRotator, arcoConfig)
        rotatorMap.put(rc.id, rotatorInstance)
        rotatorPanels.addOne(rotatorInstance.rotatorPanel)
    }
    rotators.headOption.foreach { rc =>
      selectedRotator.value = rc.id
    }
  }
}

