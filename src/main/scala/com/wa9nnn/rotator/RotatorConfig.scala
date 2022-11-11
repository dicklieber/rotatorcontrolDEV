package com.wa9nnn.rotator

import com.wa9nnn.util.HostAndPort
import play.api.libs.json.{Json, OFormat}

import java.nio.file.{Files, Path, Paths}
import scala.util.Try

import com.wa9nnn.rotator.RotatorConfig.rcfmt
import com.wa9nnn.rotator.Config.configfmt

case class RotatorConfig(name: String = "?", hostAndPort: HostAndPort = HostAndPort("192.168.0.123", 4001))

object RotatorConfig {
  implicit val rcfmt: OFormat[RotatorConfig] = Json.format[RotatorConfig]
}

case class Config(configs: List[RotatorConfig] = List.empty, rgctldPort:Int = 4533)

object Config {

  implicit val configfmt: OFormat[Config] = Json.format[Config]
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

  def write(config: Config, path: Path = defaultPath): Try[String] = {
    Try {
      Files.writeString(path, Json.prettyPrint(Json.toJson(config)))
      s"Config saved to $path"
    }
  }

  def read(path: Path = defaultPath): Try[Config] = {
    Try {
      val sJson = Files.readString(path)
      Json.parse(sJson).as[Config]
    }
  }
}