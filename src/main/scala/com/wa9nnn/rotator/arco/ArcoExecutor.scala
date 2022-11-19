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
import com.wa9nnn.rotator.{RotatorConfig, arco}
import com.wa9nnn.util.Stamped

import java.io.{DataInputStream, InputStream, OutputStream, PrintWriter}
import java.net.Socket
import java.time.Instant
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

/**
 * Sends commands and does something with the response.
 * Assumes one line command and a single line returned.
 *
 * @param rotatorConfig for an Arco.
 */
class ArcoExecutor(val rotatorConfig: RotatorConfig) extends LazyLogging {

  val name: String = rotatorConfig.name
  private var socketAndStreams: Try[SocketAndStreams] = Failure(new IllegalStateException())
  val threadPool: ExecutorService = Executors.newFixedThreadPool(1)


  def execute(arcoTask: ArcoTask): Unit = {
    threadPool.submit(arcoTask)
  }

  /**
   * Should only be invoked from an [[ArcoTask]]
   *
   * @param cmd to be sent to ARCO
   * @return response from ARCO or a [[Failure]]
   */
  def sendReceive(cmd: String): Try[String] = {
    val r: Try[String] = socketAndStreams.orElse(Try(new SocketAndStreams(rotatorConfig))).map { sas =>
      socketAndStreams = Success(sas)
      val writer = sas.writer
      writer.print(cmd)
      writer.print("\r")
      writer.flush()

      val recBuffer = new Array[Byte](500)
      val bytesRead: Int = sas.dataInputStream.read(recBuffer)
      val response = recBuffer.take(bytesRead)
      val str = new String(response).trim
      str

    }
    r
  }
}

class SocketAndStreams(val rotatorConfig: RotatorConfig) {
  val client: Socket = new Socket(rotatorConfig.host, rotatorConfig.port)
  client.setSoTimeout(5000)

  private val outputStream: OutputStream = client.getOutputStream
  val writer = new PrintWriter(outputStream)

  private val inputStream: InputStream = client.getInputStream
  val dataInputStream = new DataInputStream(inputStream)

}