package com.wa9nnn.rotorgenius.rg

import com.typesafe.scalalogging.LazyLogging
import Configuration.Configuration
import Moving.Moving
import ResponseParser.{Degree, Offset}
import com.wa9nnn.util.Stamped

/**
 * Internalized version of message returned via the "|h" request.
 * based on "4O3A RotorGenius_Protocol_Description_rev4.pdf"
 *
 * @param panic 0 if ok, otherwise undefined in
 */
case class RGHeader(
                     panic: Int,
                     rotator1: Rotator,
                     rotator2: Rotator) extends LazyLogging with Stamped {
}

/**
 * See "4O3A RotorGenius_Protocol_Description_rev4.pdf" section 1.0
 */
case class Rotator(
                    currentAzimuth: Option[Degree],
                    limitCW: Degree,
                    limitCCW: Degree,
                    configureation: Configuration,
                    moving: Moving,
                    offsetx: Offset,
                    targetAzimuth: Option[Degree],
                    startAzimuth: Option[Degree],
                    outOfLimits: Boolean,
                    name: String
                  ) extends LazyLogging {
}


object RGHeader {
  def apply(response: Array[Byte]): RGHeader = {
    implicit val parser = new ResponseParser(response)
    if (parser.nextString(2) != "|h")
      throw new IllegalArgumentException("""Response does not start with "|h"!""")

    parser.nextString(1) //ign ore obsolete active flag
    val panic = parser.nextByte()
    val r1 = Rotator.apply
    val r2 = Rotator.apply
    new RGHeader(panic, r1, r2)
  }
}

object Rotator {

  def apply(implicit parser: ResponseParser): Rotator = {

    //    parser.nextString(1) //ign ore obsolete active flag
    //    val panic = parser.nextByte() val currentAzimuth = parser.nextDegree
    val currentAzimuth = parser.nextDegree
    val limitCW = parser.nextDegree.get
    val limitCCW = parser.nextDegree.get
    val configureation = Configuration.apply
    val movingx = Moving.apply
    val offsetx = parser.nextOffset
    val targetAzimuthx = parser.nextDegree
    val startAzimuthx = parser.nextDegree
    val outOfLimits = parser.nextString(1) == "1"
    val name = parser.nextString(12)

    new Rotator(
      currentAzimuth,
      limitCW,
      limitCCW,
      configureation,
      movingx,
      offsetx,
      targetAzimuthx,
      startAzimuthx,
      outOfLimits,
      name
    )
  }
}

