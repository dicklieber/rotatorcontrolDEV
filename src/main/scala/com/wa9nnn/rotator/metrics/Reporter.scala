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

package com.wa9nnn.rotator.metrics

import com.codahale.metrics.jmx.JmxReporter
import nl.grons.metrics4.scala.DefaultInstrumented

import java.util
import javax.inject.Singleton
import javax.management.{MBeanServer, MBeanServerConnection, ObjectInstance, ObjectName}
import javax.management.remote.JMXConnector
import javax.naming.InitialContext
import scala.jdk.CollectionConverters.CollectionHasAsScala

class Reporter extends DefaultInstrumented {
  private val jmxReporter: JmxReporter = JmxReporter.forRegistry(metricRegistry).build
  jmxReporter.start()




}


