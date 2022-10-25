package com.wa9nnn.rotorgenius

import java.io._
import java.net._

object Tg extends App {


  val client = new Socket("192.168.0.16", 9006)

  private val outputStream: OutputStream = client.getOutputStream
  private val dataOutputStream = new DataOutputStream(outputStream)


  private val inputStream: InputStream = client.getInputStream
  private val dataInputStream = new DataInputStream(inputStream)
  val recBuffer = new Array[Byte](500)

  while (true) {
    dataOutputStream.writeBytes("|h")

    val bytesRead: Int = dataInputStream.read(recBuffer)
    val response = recBuffer.take(bytesRead)
    val header: RGHeader = RGHeader(response)
    println(header)
    Thread.sleep(1000)
  }
}