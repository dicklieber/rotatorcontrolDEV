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
import com.wa9nnn.rotator.arco.ArcoTask.handleLast
import com.wa9nnn.util.Stamped
import nl.grons.metrics4.scala.{DefaultInstrumented, Meter}

import scala.util.{Failure, Success, Try}


/**
 * One discrete operation with an Arco controller
 * This is run in a queue so only one can happen one at a time.
 *
 * @param cmd                 to send
 * @param f                   function that will process the result.
 * @param arcoExecutor        work gets done.
 */
case class ArcoTask(cmd: String)(f: String => Unit)(implicit arcoExecutor: ArcoExecutor) extends Runnable
  with LazyLogging {
  override def run(): Unit = {
    val triedString: Try[String] = arcoExecutor.sendReceive(cmd)
    handleLast(triedString, arcoExecutor)
    triedString match {
      case Failure(_) =>
      case Success(value) =>
        f(value)
    }
  }
}

object ArcoTask extends LazyLogging with DefaultInstrumented{
  private val arcoTaskMeter: Meter = metrics.meter("ArcoTask")


  def pollsPerSeconds:Double = {
    arcoTaskMeter.oneMinuteRate
  }
  var lastFailure: LastFailure = LastFailure()

  def handleLast(triedString: Try[String], arcoExecutor: ArcoExecutor): Unit = {
    arcoTaskMeter.mark()
//logger.debug(f"arcoTaskMeter.oneMinuteRate: {}", arcoTaskMeter.oneMinuteRate)

    if (lastFailure.newResult(triedString)) {
      val message = triedString match {
        case Failure(exception) =>
          exception.getMessage
        case Success(_) =>
          "success"
      }
      logger.info(s"${arcoExecutor.name} is now $message")
    }
  }
}

case class LastFailure(triedString: Try[String] = Failure(new IllegalStateException())) extends Stamped {
  def newResult(candidate: Try[String]): Boolean = {
    candidate == triedString
  }

  override def toString: String = {
    triedString match {
      case Failure(exception) =>
        exception.getMessage
      case Success(value) =>
        value
    }
  }
}
