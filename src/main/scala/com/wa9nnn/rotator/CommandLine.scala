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

package com.wa9nnn.rotator

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotator.CommandLine.{defaultRotatorGeniusPort, defaultRotctldPort}
import com.wa9nnn.util.HostAndPort

/**
 * What we got from the command line.
 *
 * @param controllerHostAndPort netwoerk address of Rotator Genius
 * @param rotctldPort   rotctld port to listen on.
 * @param verbose       lots of outout.
 * @param debug         show some stuff.
 */
case class CommandLine(
                        controllerHostAndPort: HostAndPort = HostAndPort("not defined", defaultRotatorGeniusPort),
                        rotctldPort: Int = defaultRotctldPort,
                        verbose: Boolean = false,
                        debug: Boolean = false
                      ) extends LazyLogging {
  def ifVerbose(c: Unit): Unit = {
    c
  }

  def ifDebug(c: Unit): Unit = {
    c
  }
}

object CommandLine {
  val defaultRotatorGeniusPort = 9006
  val defaultRotctldPort = 4533
}
