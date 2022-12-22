package com.wa9nnn.rotator

import com.wa9nnn.util.HostAndPort
import org.specs2.mutable.Specification

import java.nio.file.{Files, Path}
import scala.util.Try

class ConfigManagerSpec extends Specification {

  "ConfigManagerSpec" should {
    "round trip" in {
      val path = Files.createTempDirectory("ConfigManagerSpec")
      val configManager = new ConfigManager(path)

      val r1 = RotatorConfig("test1", HostAndPort("192.168.0.123", 4001))
      val r2 = RotatorConfig("test1", HostAndPort("10.10.0.1", 4001))
      val config = AppConfig(List(r1, r2))
      val triedString = configManager.write(config)
      triedString must beASuccessfulTry[String]

      val backAgain: AppConfig = new ConfigManager(path).value
      backAgain must beEqualTo (config)
    }
  }
}
