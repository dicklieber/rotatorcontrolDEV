package com.wa9nnn.rotorgenius.rg

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotorgenius.CommandLine
import com.wa9nnn.rotorgenius.rg.ResponseParser.Degree
import com.wa9nnn.util.HostAndPort

import java.io.{DataInputStream, DataOutputStream, InputStream, OutputStream}
import java.net.Socket
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

class RotatorGeniusInterface(commandLine: CommandLine) extends Runnable with LazyLogging {

  var currentHeader: Option[RGHeader] = None
  private var listeners: Set[Headerlistener] = Set.empty
   var currentRotator = 0

  private val executor = new ScheduledThreadPoolExecutor(1)
  executor.scheduleWithFixedDelay(this, 1000, 250, TimeUnit.MILLISECONDS)
  private val rotatorGenius: HostAndPort = commandLine.rotatorGenius
  val client: Socket = new Socket(rotatorGenius.toInetAddress, rotatorGenius.port)

  private val outputStream: OutputStream = client.getOutputStream
  private val dataOutputStream = new DataOutputStream(outputStream)

  private val inputStream: InputStream = client.getInputStream
  private val dataInputStream = new DataInputStream(inputStream)

  def addListener(headerlistener: Headerlistener): Unit = {
    listeners = listeners + headerlistener
  }

  def setCurrentRotato(index:Int) :Unit  = {
    assert(index < 2, "Current rotator can onlyh be 0 or 1")
    currentRotator = index
  }

  def getPosition: Option[Degree] = {
    for {
      header: RGHeader <- currentHeader
      rotator: Rotator = header.rotators(currentRotator)
      d <- rotator.currentAzimuth
    } yield {
      d
    }
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

    dataOutputStream.writeBytes("|h")

    val bytesRead: Int = dataInputStream.read(recBuffer)
    val response = recBuffer.take(bytesRead)
    val incomingHeader: RGHeader = RGHeader(response)

    currentHeader = Option(incomingHeader)
    listeners.foreach(_.newHeader(incomingHeader))
  }
}

trait Headerlistener {
  def newHeader(RGHeader: RGHeader): Unit
}
