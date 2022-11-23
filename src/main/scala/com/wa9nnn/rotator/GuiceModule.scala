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

import com.google.inject.AbstractModule
import com.wa9nnn.rotator.arco.ArcoCoordinator
import com.wa9nnn.rotator.metrics.Reporter
import com.wa9nnn.rotator.rotctld.RotctldServer
import net.codingwell.scalaguice.ScalaModule

class GuiceModule() extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[ConfigManager].asEagerSingleton()
    bind[ArcoCoordinator].asEagerSingleton()
    bind[Reporter].asEagerSingleton()
    bind[RotctldServer].asEagerSingleton()
  }
}