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

package com.wa9nnn.rotator.rotctld

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotator.{ConfigManager, Degree}
import com.wa9nnn.rotator.arco.ArcoCoordinator
import scalafx.concurrent
import scalafx.concurrent.Task

import java.io.{InputStreamReader, LineNumberReader}
import java.net.{ServerSocket, Socket, SocketException}
import javax.inject.Inject

class RotctldServer @Inject()(configManager: ConfigManager, arcoCoordinator: ArcoCoordinator) extends LazyLogging {
  logger.info("starting RotctldServer")

  val task: Task[String] = Task.apply {
    val serverSocket = new ServerSocket(configManager.value.rgctldPort)

    val parser = """(\+)?[\\|]?(.+)""".r
    val setPosRegx = """set_pos (\d+\.\d+) (\d+\.\d+)""".r

    def get_pos(implicit extended: Boolean): String = {
      val currentAzimuth: Degree = arcoCoordinator.selectedRotatorAzimuth
      if (extended) {
        s"""get_pos:
           |Azimuth: $currentAzimuth
           |Elevation: 45.000000
           |RPRT 0""".stripMargin
      } else {
        s"""217.80
           |0.00
           |""".stripMargin
      }
    }

    def get_info(implicit extended: Boolean): String = {
      if (extended) {
        s"""get_info:
           |Info: None
           |RPRT 0""".stripMargin
      } else {
        s"""None
           |""".stripMargin
      }
    }

    def set_pos(azi: String, ele: String)(implicit extended: Boolean): String = {
      //todo do move
      val targetAzimuth: Degree = Degree(azi)
      arcoCoordinator.moveSelected(targetAzimuth)

      if (extended) {
        s"""set_pos: $azi $ele
           |RPRT 0""".stripMargin
      } else {
        s"""None
           |""".stripMargin
      }

    }

    while (true) {
      try {
        val socket: Socket = serverSocket.accept()

        logger.info("accepted connection : {}", socket.getInetAddress)
        val reader: LineNumberReader = new LineNumberReader(new InputStreamReader(socket.getInputStream))
        implicit val outputStream = socket.getOutputStream

        try {
          while (true) {
            val command = reader.readLine()
            logger.trace("command: {}", command)

            val result = try {
              val parser(ex, va) = command
              implicit val extended: Boolean = ex.nonEmpty

              va match {
                case "_" | "get_info" =>
                  get_info
                case "p" | "get_pos" =>
                  get_pos

                case setPosRegx(azi, ele) =>
                  set_pos(azi, ele)

                case x =>
                  logger.info("unexpected: {}", x)
                  s"unexpected: $x"
              }
            } catch {
              case exception: Exception =>
                exception.getMessage
            }
            logger.debug("result: {}", result)
            outputStream.write(result.getBytes)
            outputStream.flush()
          }
        } catch {
          case so: SocketException =>
            logger.error("socket: {} from: {}", so.getMessage, socket.getInetAddress)
        }
        //      socket.close()
      } catch {
        case e: Exception =>
          //        logger.whenDebugEnabled{
          //          logger.trace()
          //        }
          logger.info("Done with socket", e.getMessage)
      }
    }
    "Should never get here"

  }

  private val service: concurrent.Service[String] = concurrent.Service(task)
  service.start()

}


