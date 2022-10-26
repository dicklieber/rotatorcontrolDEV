package com.wa9nnn.rotorgenius

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotorgenius.ResponseParser.Degree

import java.io.{DataInputStream, DataOutputStream, InputStream, OutputStream}
import java.net.Socket
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

class DeviceEngine() extends Runnable with LazyLogging {

  private var currentHeader: Option[RGHeader] = None

  private val executor = new ScheduledThreadPoolExecutor(1)
  executor.scheduleWithFixedDelay(this, 1000, 250, TimeUnit.MILLISECONDS)
  val client = new Socket("192.168.0.16", 9006)

  private val outputStream: OutputStream = client.getOutputStream
  private val dataOutputStream = new DataOutputStream(outputStream)


  private val inputStream: InputStream = client.getInputStream
  private val dataInputStream = new DataInputStream(inputStream)

def getPosition:Option[Degree]= {
  for {
    header: RGHeader <- currentHeader
    rotator: Rotator = header.rotator1
    d <- rotator.currentAzimuth
  }yield{
    d
  }
}

  //  while (true) {
  //    dataOutputStream.writeBytes("|h")
  //
  //    val bytesRead: Int = dataInputStream.read(recBuffer)
  //    val response = recBuffer.take(bytesRead)
  //    val header: RGHeader = RGHeader(response)
  //    println(header)
  //    Thread.sleep(1000)
  //  }

  override def run(): Unit = {
    //todo handle fail and not initialized,
    val recBuffer = new Array[Byte](500)

    dataOutputStream.writeBytes("|h")

    val bytesRead: Int = dataInputStream.read(recBuffer)
    val response = recBuffer.take(bytesRead)
    val header: RGHeader = RGHeader(response)
    currentHeader = Option(header)

  }
}
