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

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotator.{Degree, RotatorConfig, RotatorInterface}
import scalafx.beans.property.ObjectProperty

import java.util.UUID
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

/**
 *
 * Handles move command and polls current azimuth from an Arco controller updating the rotatorStateProperty
 *
 * Currently only supports move via move (M090 command) and (C command) via ScheduledThreadPoolExecutor.
 *
 * Uses the Yaesu GSM-232a protocol over IP see https://www.yaesu.com/downloadFile.cfm?FileID=820&FileCatID=155&FileName=GS232A.pdf&FileContentType=application%2Fpdf
 *
 * See [[./docs/GS232A.pdf]]
 *
 * @param rotatorConfig        details about this ARCO.
 * @param rotatorStateProperty where to put ARCO state.
 */
class ArcoInterface(rotatorConfig: RotatorConfig, arcoConfig:Config, rotatorStateProperty: ObjectProperty[RotatorState]) extends ScheduledThreadPoolExecutor(1)
  with RotatorInterface with LazyLogging with Runnable {
  val id: UUID = rotatorConfig.id

  def stop(): Unit = shutdown()

  private implicit val arcoExecutor = new ArcoQueue(rotatorConfig, arcoConfig)
  private val pollMs: Long = arcoConfig.getDuration("poll", TimeUnit.MILLISECONDS)
  // schedule polling ARCO
  scheduleWithFixedDelay(this, 100, pollMs.toInt, TimeUnit.MILLISECONDS)

  /**
   *
   * @param targetAzimuth what to do
   * @return "ok" or exception
   */
  def move(targetAzimuth: Degree): Unit = {
    logger.debug(s"Move command: {}}", targetAzimuth.toString)
    val cmd = s"M${targetAzimuth.threeDigits}"
    arcoExecutor.execute(ArcoOperation(cmd, (result: String) =>
      logger.info("Move target: targetAzimuth:{} result: {}", targetAzimuth, result)
    ))
  }

  override def run(): Unit = {
    try {
      arcoExecutor.execute(ArcoOperation("C", (response: String) => {
        val str = new String(response).trim
        logger.trace(s"""Cmd: C Response: "$str"""")
        str match {
          case s"""+$azi""" =>
            val rotatorState = RotatorState(Degree(azi))
            rotatorStateProperty.value = rotatorState.copy(currentAzimuth = Degree(0))
            rotatorStateProperty.value = rotatorState
          case x =>
            logger.error(s"""Bad response got $x expecting something like: "+0123""")
        }
      }))
    } catch {
      case e:Throwable =>
        logger.error("Run level exception ", e)
    }
  }
}
