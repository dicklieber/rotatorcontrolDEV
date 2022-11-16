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

import com.wa9nnn.util.HostAndPort
import play.api.libs.json.{Json, OFormat}

import java.nio.file.{Files, Path, Paths}
import scala.util.Try
import com.wa9nnn.rotator.RotatorConfig.rcfmt
import com.wa9nnn.rotator.AppConfig.configfmt
import scalafx.beans.property.{IntegerProperty, ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer

import java.util.UUID

case class RotatorConfig(name: String = "?", host: String = "192.168.0.123", port: Int = 4001, id: UUID = UUID.randomUUID()) {

  lazy val nameProperty = new StringProperty(this, "name", name)
  lazy val hostProperty = new StringProperty(this, "host", host)
  lazy val portProperty = new IntegerProperty(this, "port", port)

  def collect: RotatorConfig = {
    RotatorConfig(nameProperty.value, hostProperty.value, portProperty.value.toInt)
  }
}

object RotatorConfig {
  implicit val rcfmt: OFormat[RotatorConfig] = Json.format[RotatorConfig]
}

case class AppConfig(rotators: List[RotatorConfig] = List.empty, rgctldPort: Int = 4533) {
  def addRotator(): AppConfig = {
    copy(rotators = rotators.appended(RotatorConfig("new")))
  }

  def collect: AppConfig = {
    new AppConfig(rotators.map {
      _.collect
    }, rotctldPortProperty.value.toInt)
  }

  def remove(uuid: UUID): AppConfig = {
    val b4 = rotators.size
    val result = copy(rotators = rotators.filterNot(_.id == uuid))
    assert(b4 > result.rotators.size, "Didn't remove rotator")
    result
  }

  lazy val rotctldPortProperty: IntegerProperty = IntegerProperty(rgctldPort)

}

object AppConfig {

  implicit val configfmt: OFormat[AppConfig] = Json.format[AppConfig]
}

object ConfigManager {

  import HostAndPort.hostAndPortFormat
  import com.wa9nnn.rotator.RotatorConfig.rcfmt

  lazy val defaultPath: Path = {
    val home = Paths.get(System.getProperty("user.home"))
    home.resolve("rotatorcontrol")
      .resolve("config.json")
  }
  Files.createDirectories(defaultPath.getParent)

  def write(config: AppConfig, path: Path = defaultPath): Try[String] = {
    Try {
      Files.writeString(path, Json.prettyPrint(Json.toJson(config)))
      s"Config saved to $path"
    }
  }

  def read(path: Path = defaultPath): Try[AppConfig] = {
    Try {
      val sJson = Files.readString(path)
      Json.parse(sJson).as[AppConfig]
    }
  }
}