package com.wa9nnn.rotorgenius.arco

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotorgenius.CommandLine
import com.wa9nnn.rotorgenius.rg.{Move, RGHeader, Rotator}
import com.wa9nnn.rotorgenius.rg.ResponseParser.Degree
import com.wa9nnn.util.HostAndPort

import java.io.{DataInputStream, DataOutputStream, InputStream, OutputStream}
import java.net.Socket
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

class ArcoInterface(commandLine: CommandLine) extends Runnable with LazyLogging {

  var currentAzimuth: Option[Degree] = None
  private var listeners: Set[Headerlistener] = Set.empty

  private val executor = new ScheduledThreadPoolExecutor(1)
  executor.scheduleWithFixedDelay(this, 1000, 250, TimeUnit.MILLISECONDS)
  private val rotatorGenius: HostAndPort = commandLine.controllerHostAndPort
  val client: Socket = new Socket(rotatorGenius.toInetAddress, rotatorGenius.port)

  private val outputStream: OutputStream = client.getOutputStream
  private val dataOutputStream = new DataOutputStream(outputStream)

  private val inputStream: InputStream = client.getInputStream
  private val dataInputStream = new DataInputStream(inputStream)

  def addListener(headerlistener: Headerlistener): Unit = {
    listeners = listeners + headerlistener
  }


  def getPosition: Option[Degree] = {
    currentAzimuth
  }

  /**
   *
   * @param move what to do
   * @return None if command excepted otherwise error message
   */
  def move(move: Move): Option[String] = {
    val recBuffer = new Array[Byte](500)
    dataOutputStream.write(move.rgCommand)
    val bytesRead: Int = dataInputStream.read(recBuffer)
    val response = recBuffer.take(bytesRead)
    Move.checkResult(response)
  }

  override def run(): Unit = {
    //todo handle fail and not initialized,
    val recBuffer = new Array[Byte](500)

    dataOutputStream.writeBytes("C\r")

    val bytesRead: Int = dataInputStream.read(recBuffer)
    val response = recBuffer.take(bytesRead)
    val str = new String(response).trim

    str match {
      case s"""+$azi""" =>
        currentAzimuth = Option(azi.toInt)
      case x =>
        logger.error("""Bad response got $x expecting "+0123""")
    }
  }
}

object ArcoTest extends App with LazyLogging {
  private val commandLine = CommandLine(HostAndPort("192.168.0.123", 4001))
  private val arcoInterface = new ArcoInterface(commandLine)

  while (true) {
    Thread.sleep(125)
    logger.info(s"Azimuth: ${arcoInterface.currentAzimuth}")
  }

}

trait Headerlistener {
  def newHeader(RGHeader: RGHeader): Unit
}
