package com.wa9nnn.rotorgenius

import com.typesafe.scalalogging.LazyLogging

import java.io.{InputStreamReader, LineNumberReader}
import java.net.{ServerSocket, Socket, SocketException}

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

      logger.info("accepted connection : {}", socket.getInetAddress)
      val reader: LineNumberReader = new LineNumberReader(new InputStreamReader(socket.getInputStream))
      implicit val outputStream = socket.getOutputStream

      try {
        while (true) {
          val command = reader.readLine()
          logger.trace("command: {}", command)

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
      } catch {
        case so:SocketException =>
          logger.error("socket: {} from: {}", so.getMessage, socket.getInetAddress)
      }
      //      socket.close()
    } catch {
      case e: Exception =>
//        logger.whenDebugEnabled{
//          logger.trace()
//        }
        logger.info("Done with socket")
    }
  }


}

object RotctldServer extends App {
  private val server = new RotctldServer
}



