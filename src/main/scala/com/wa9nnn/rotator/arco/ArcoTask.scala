package com.wa9nnn.rotator.arco

import com.typesafe.scalalogging.LazyLogging

/**
 * One discrete operation with an Arco controller
 * This is run in a queue so only one can happen at a time.
 *
 * @param cmd          to send
 * @param f            function that will process the result.
 * @param arcoExecutor what talks to the Arco controller
 */
case class ArcoTask(cmd: String)(f: String => Unit)(implicit arcoExecutor: ArcoExecutor) extends Runnable with LazyLogging {
  override def run(): Unit = {
    try {
      val result = arcoExecutor.sendReceive(cmd)
      f(result)
    } catch {
      case exception: Exception =>
        logger.error(s"cmd: $cmd", exception)
    }
  }
}

