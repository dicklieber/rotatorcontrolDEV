package com.wa9nnn.rotorgenius

import com.typesafe.scalalogging.LazyLogging

import java.io.{BufferedReader, InputStream}
import java.net.{ServerSocket, Socket}
import scala.io.Source

class RotctldServer extends LazyLogging {

  private val serverSocket = new ServerSocket(4534)

  //  val long2Short = Seq(
  //    "get_info" -> "_",
  //    "get_pos" -> "p",
  //  )

  val parser = """(\+)?\\?(.+)""".r

  def get_pos(implicit extended: Boolean): String = {
    if (extended) {
      s"""get_pos:
         |Azimuth: 33.3
         |Elevation: 45.000000
         |RPRT 0""".stripMargin
    } else {
      s"""217.80
         |0.00
         |""".stripMargin
    }
  }

  def get_info(implicit extended: Boolean): String = {
    if (extended) {
      s"""get_info:
         |Info: None
         |RPRT 0""".stripMargin
    } else {
      s"""None
         |""".stripMargin
    }
  }

  while (true) {
    try {
      val socket: Socket = serverSocket.accept()

      val set = socket.supportedOptions()
      println(set)
      logger.debug("accepted connection : {}", socket.getInetAddress)

      val commands = Source.fromInputStream(socket.getInputStream).getLines()
      implicit val outputStream = socket.getOutputStream

      commands.foreach { command =>
        logger.debug("command: {}", command)

        val result = try {
          val parser(ex, va) = command
          implicit val extended: Boolean = ex.nonEmpty

          va match {
            case "_" | "get_info" =>
              get_info
            case "p" | "get_pos" =>
              get_pos
            case x =>
              logger.info("unexpected: {}", x)
              s"unexpected: $x"
          }
        } catch {
          case exception: Exception =>
            exception.getMessage
        }
        logger.debug("result: {}", result)
        outputStream.write(result.getBytes)
        outputStream.flush()
      }
      //      socket.close()
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }


}

object RotctldServer extends App {
  private val server = new RotctldServer
}



