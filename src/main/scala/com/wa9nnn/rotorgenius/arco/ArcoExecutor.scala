package com.wa9nnn.rotorgenius.arco

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.util.HostAndPort

import java.io.{DataInputStream, InputStream, OutputStream, PrintWriter}
import java.net.Socket
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

/**
 * Sends commands and does something with the response.
 * Assumes one line command and a single line returned.
 *
 * @param hostAndPort of an ARCO.
 */
class ArcoExecutor(hostAndPort: HostAndPort) extends LazyLogging {
  implicit val ec: ExecutionContext = new ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(1)

    override def execute(runnable: Runnable): Unit = {
      threadPool.submit(runnable)
    }

    override def reportFailure(t: Throwable): Unit = {}
  }

  //  private val executor = new ScheduledThreadPoolExecutor(1)
  //  executor.scheduleWithFixedDelay(this, 1000, 250, TimeUnit.MILLISECONDS)
  val client: Socket = new Socket(hostAndPort.toInetAddress, hostAndPort.port)
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
