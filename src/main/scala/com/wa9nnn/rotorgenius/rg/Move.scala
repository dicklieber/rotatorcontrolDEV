package com.wa9nnn.rotorgenius.rg

import com.wa9nnn.rotorgenius.rg.ResponseParser.Degree

/**
 * Command to move rotator
 * @param rotator 1 or 2
 * @param targetAzimuth 0-360
 */
case class Move(rotator: Int, targetAzimuth: Degree) {
  if (rotator < 1 || rotator > 2)
    throw new IllegalArgumentException(s"Rotator must be 1 or 2. but got $rotator")

  if (targetAzimuth < 0 || targetAzimuth > 360)
    throw new IllegalArgumentException(s"Rotator must be 0 to 360. but got $targetAzimuth")

  def rgCommand: Array[Byte] = {
    val s = f"|A$rotator%1d$targetAzimuth%03d"
    s.getBytes
  }
}

object Move {
  def checkResult(result: Array[Byte]): Option[String] = {
    val str = new String(result)
    Option.when(str != "|AK") {
      s"""Move failed expected "|AK" but got "$str""""
    }
  }

}
