package com.wa9nnn.rotator.rg

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotator.rg.ResponseParser.{Degree, Offset}

import java.nio.ByteBuffer

class ResponseParser(response: Array[Byte]) extends LazyLogging {

  private val byteBuffer: ByteBuffer = ByteBuffer.wrap(response)

  def nextString(length: Int): String = {
    val buffer = new Array[Byte](length)
    byteBuffer.get(buffer)
    new String(buffer).trim
  }

  def nextByte():Int = {
    val b: Byte = byteBuffer.get()
    b.toInt
  }

  def nextDegree(implicit parser: ResponseParser): Option[Degree] = {
    parser.nextString(3).toInt match {
      case 999 =>
        None
      case d if d >= 0 && d <= 360 =>
        Option(d)
      case x =>
        throw new IllegalArgumentException(s"Expected direction 0-360 but got $x!")
    }
  }

  def nextOffset(implicit parser: ResponseParser): Offset = {
    parser.nextString(4).toInt match {
      case d if d >= 0 - 180 && d <= 180 =>
        d
      case x =>
        throw new IllegalArgumentException(s"Expected offset -180 to 180 but got $x!")
    }
  }
}

object ResponseParser {
  type Degree = Int
  type Offset = Int
}

object Configuration extends Enumeration with LazyLogging {
  type Configuration = Value
  val Azimuith, Elevation = Value

  def apply(implicit parser: ResponseParser): Configuration = {
    parser.nextString(1) match {
      case "A" => Azimuith
      case "E" => Elevation
      case x =>
        logger.warn(s"Expecting A or E but got $x, assuming Azimuith!")
        Azimuith
    }
  }
}

object Moving extends Enumeration with LazyLogging {
  type Moving = Value
  val Stopped, CW, CCW = Value

  def apply(implicit parser: ResponseParser): Moving = {
    parser.nextString(1) match {
      case "0" => Stopped
      case "1" => CW
      case "2" => CCW
      case x =>
        logger.warn(s"Expecting 0-2  but got $x, assuming Stopped!")
        Stopped
    }
  }
}
