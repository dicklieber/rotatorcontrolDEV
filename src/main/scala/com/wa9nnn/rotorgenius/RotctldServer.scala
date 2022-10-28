package com.wa9nnn.rotorgenius

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotorgenius.rg.ResponseParser.Degree
import com.wa9nnn.rotorgenius.rg.{Move, RotatorGeniusInterface}

import java.io.{InputStreamReader, LineNumberReader}
import java.net.{ServerSocket, Socket, SocketException}

class RotctldServer(commandLine: CommandLine, deviceEngine: RotatorGeniusInterface) extends LazyLogging {
  logger.info("starting RotctldServer")

  private val serverSocket = new ServerSocket(commandLine.rotctldPort)

  private val parser = """(\+)?[\\|]?(.+)""".r
  private val setPosRegx = """set_pos (\d+\.\d+) (\d+\.\d+)""".r

  def get_pos(implicit extended: Boolean): String = {
    val maybeCurrentAzumuth: Option[Degree] = deviceEngine.getPosition
    if (extended) {
      s"""get_pos:
         |Azimuth: ${maybeCurrentAzumuth.getOrElse("?")}
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

  def set_pos(azi:String, ele:String)(implicit extended: Boolean):String = {
    //todo do move
    val iAzi = azi.toDouble.toInt
    deviceEngine.move(Move(1, iAzi))

    if (extended) {
      s"""set_pos: $azi $ele
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

              case setPosRegx(azi, ele) =>
                set_pos(azi, ele)

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



