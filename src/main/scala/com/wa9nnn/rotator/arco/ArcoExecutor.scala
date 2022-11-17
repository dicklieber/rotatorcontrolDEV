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

import java.io.{DataInputStream, InputStream, OutputStream, PrintWriter}
import java.net.Socket
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

/**
 * Sends commands and does something with the response.
 * Assumes one line command and a single line returned.
 *
 * @param rotatorConfig for an Arco.
 */
class ArcoExecutor(val rotatorConfig: RotatorConfig) extends LazyLogging {
  implicit val ec: ExecutionContext = new ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(1)

    override def execute(runnable: Runnable): Unit = {
      threadPool.submit(runnable)
    }

    override def reportFailure(t: Throwable): Unit = {}
  }

  val client: Socket = new Socket(rotatorConfig.host, rotatorConfig.port)
  client.setSoTimeout(5000)

  private val outputStream: OutputStream = client.getOutputStream
  private val writer = new PrintWriter(outputStream)

  private val inputStream: InputStream = client.getInputStream
  private val dataInputStream = new DataInputStream(inputStream)

  def execute(arcoTask: ArcoTask): Unit = {
    ec.execute(arcoTask)
  }

  def sendReceive(cmd: String): String = {
    sendCmd(cmd)
    readResult()
  }

  private def sendCmd(cmd: String): Unit = {
    writer.print(cmd)
    writer.print("\r")
    writer.flush()
  }

  private def readResult(): String = {
    val recBuffer = new Array[Byte](500)

    val bytesRead: Int = dataInputStream.read(recBuffer)
    val response = recBuffer.take(bytesRead)
    val str = new String(response).trim
    str
  }


}
