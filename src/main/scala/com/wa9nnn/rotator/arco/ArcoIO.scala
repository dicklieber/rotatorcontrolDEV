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

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotator.RotatorConfig
import com.wa9nnn.util.Stamped

import java.io.{InputStream, PrintWriter}
import java.net.{InetSocketAddress, Socket}
import scala.util.{Failure, Success, Try}


case class ArcoIO(socket: Socket) {
  def doOperation(arcoTask: ArcoOperation ): Unit = {
    writer.print(arcoTask.cmd)
    writer.print("\r")
    writer.flush()

    val recBuffer = new Array[Byte](500)
    val bytesRead: Int = inputStream.read(recBuffer)
    val response = recBuffer.take(bytesRead)
    val str = new String(response).trim
    arcoTask.fn(str)
  }

  def close(): Unit = socket.close()

  val writer = new PrintWriter(socket.getOutputStream)

  val inputStream: InputStream = socket.getInputStream
}

object ArcoIO extends LazyLogging {
//  private val arcoTaskMeters: TrieMap[UUID, Meter] = TrieMap[UUID, Meter] ()
//  metrics.meter("ArcoTask")

  def connect(rotatorConfig: RotatorConfig): Try[ArcoIO] = {
    val name = rotatorConfig.name
    val r = Try {

      val socket = new Socket()
      val inetSocketAddress = new InetSocketAddress(rotatorConfig.host, rotatorConfig.port)
      socket.connect(inetSocketAddress, 100)
      socket.setSoTimeout(1000)
      logger.info("\t{} Connected", name)
      new ArcoIO(socket)
    }
    r
  }

  case class LastFailure(triedString: Try[String] = Failure(new IllegalStateException())) extends Stamped {
    def newResult(candidate: Try[String]): Boolean = {
      candidate == triedString
    }

    override def toString: String = {
      triedString match {
        case Failure(exception) =>
          exception.getMessage
        case Success(value) =>
          value
      }
    }
  }
}

