package com.wa9nnn.rotator.arco

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotator.rg.ResponseParser.Degree
import com.wa9nnn.rotator.rg.{RGHeader, Rotator}
import com.wa9nnn.rotator.{CommandLine, RotatorConfig, RotatorInterface}
import com.wa9nnn.util.HostAndPort

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

/**
 * Handles move command and polls current azimuth from an Arco controller
 * @param commandLine
 */
class ArcoInterface(rotatorConfig: RotatorConfig) extends ScheduledThreadPoolExecutor(1)
  with RotatorInterface with LazyLogging with Runnable {

  var currentAzimuth: Option[Degree] = None
  private var listeners: Set[Headerlistener] = Set.empty

  private implicit val arcoExecutor = new ArcoExecutor(rotatorConfig)

  def addListener(headerlistener: Headerlistener): Unit = {
    listeners = listeners + headerlistener
  }

  def getPosition: Option[Degree] = {
    currentAzimuth
  }

  scheduleWithFixedDelay(this, 200, 1370, TimeUnit.MILLISECONDS)

  /**
   *
   * @param targetAzimuth what to do
   * @return "ok" or exception
   */
  def move(targetAzimuth: Degree): Unit = {
    val cmd = f"M$targetAzimuth%03d"
    arcoExecutor.execute(ArcoTask(cmd) { result: String =>
      logger.trace("Move result: {}", result)
    })
  }

  override def run(): Unit = {
    //todo handle fail and not initialized

    val task = ArcoTask("C") { response =>
      val str = new String(response).trim
      logger.trace(s"""Cmd: C Response: "$str"""")
      str match {
        case s"""+$azi""" =>
          currentAzimuth = Option(azi.toInt)
          val rgHeader = new RGHeader(rotators = List(Rotator(currentAzimuth = currentAzimuth)))
          listeners.foreach(_.newHeader(rgHeader))
        case x =>
          logger.error(s"""Bad response got $x expecting something like: "+0123""")
      }
    }
    arcoExecutor.execute(task)
  }
}

trait Headerlistener {
  def newHeader(RGHeader: RGHeader): Unit
}



