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
import com.wa9nnn.rotator.RotatorConfig
import com.wa9nnn.rotator.arco.ArcoQueue.initialState

import java.util.concurrent.{ExecutorService, Executors}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

/**
 * Coordinates operations with an ARCO Controller.
 *
 * @param rotatorConfig for an Arco.
 *                      @param application config from [[src/main/resources/reference.conf]]
 */
class ArcoQueue(val rotatorConfig: RotatorConfig, arcoConfig:Config) extends LazyLogging {
  private val name = rotatorConfig.name
  private var lastTriedArcoIO: Try[ArcoIO] = initialState
  private val threadPool: ExecutorService = Executors.newFixedThreadPool(1,
    (r: Runnable) => {
      new Thread(r, "ArcoExecutor")
    })


  def execute(arcoOperation: ArcoOperation): Unit = {
    threadPool.submit(ArcoTask(arcoOperation))
  }


  case class ArcoTask(arcoOperation: ArcoOperation) extends Runnable {

    def handleStateChange(maybeNow: Try[ArcoIO]): Unit = {
      implicit def t2s(t: Try[ArcoIO]): String =
        t match {
          case Failure(exception) =>
            exception.getMessage
          case Success(_) =>
            "Connected"
        }

      val last: String = lastTriedArcoIO
      val now: String = maybeNow

      if (last != now) {
        logger.info(s"$name change from $last to $now")
      }
      lastTriedArcoIO = maybeNow
    }

    override def run(): Unit = {
      var maybeNow: Try[ArcoIO] = lastTriedArcoIO.orElse(ArcoIO.connect(rotatorConfig, arcoConfig))
      try {

        maybeNow.foreach { arcoIO =>
          arcoIO.doOperation(arcoOperation)
        }
      } catch {
        case a: ArcoException =>
          logger.error(s"$name: ${a.reason}")
          maybeNow.foreach(_.socket.close())
          maybeNow = Failure(a)
      }

      handleStateChange(maybeNow)

    }
  }
}

object ArcoQueue {
  val initialState: Failure[Nothing] = Failure(new IllegalArgumentException("Startup"))
}
