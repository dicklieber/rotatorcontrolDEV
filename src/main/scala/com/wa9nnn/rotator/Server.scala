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

import com.wa9nnn.rotator.arco.ArcoInterface
import com.wa9nnn.util.HostAndPort


class Server(config: Config) {


   val rotatorInterface: RotatorInterface = new ArcoInterface(RotatorConfig("default", HostAndPort("192.168.0.16", 4001)))
//  private val rotatorInterface = new RotatorGeniusInterface(commandLine)
//  new SwingTest(rotatorInterface)

  private val rotctldThread = new Thread("rotctld") {
    override def run(): Unit = {
      new RotctldServer(config.rgctldPort, rotatorInterface)
    }
  }
  rotctldThread.setDaemon(true)
  rotctldThread.start()

}
