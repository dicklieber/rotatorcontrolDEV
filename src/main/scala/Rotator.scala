import Configuration.Configuration
import Moving.Moving
import ResponseParser.{Degree, Offset}
import com.typesafe.scalalogging.LazyLogging

case class Rotator(
                    currentAzimuth: Option[Degree],
                    limitCW: Degree,
                    limitCCW: Degree,
                    configureation: Configuration,
                    movingx: Moving,
                    offsetx: Offset,
                    targetAzimuthx: Option[Degree],
                    startAzimuthx: Option[Degree],
                    outOfLimits: Boolean,
                    name: String
                  ) extends LazyLogging {
}

case class Header(
                   panic: Int,
                   rotator1: Rotator,
                   rotator2: Rotator) extends LazyLogging {
}


object Header {
  def apply(response: Array[Byte]): Header = {
    implicit val parser = new ResponseParser(response)
    if (parser.nextString(2) != "|h")
      throw new IllegalArgumentException("""Response does not start with "|h"!""")

    parser.nextString(1) //ign ore obsolete active flag
    val panic = parser.nextByte()
    val r1 = Rotator.apply
    val r2 = Rotator.apply
    new Header(panic, r1, r2)
  }
}

object Rotator {

  def apply(implicit parser: ResponseParser): Rotator = {

    //    parser.nextString(1) //ign ore obsolete active flag
    //    val panic = parser.nextByte() val currentAzimuth = parser.nextDegree
    val currentAzimuth = parser.nextDegree
    val limitCW = parser.nextDegree.get
    val limitCCW = parser.nextDegree.get
    val configureation = Configuration.apply
    val movingx = Moving.apply
    val offsetx = parser.nextOffset
    val targetAzimuthx = parser.nextDegree
    val startAzimuthx = parser.nextDegree
    val outOfLimits = parser.nextString(1) == "1"
    val name = parser.nextString(12)

    new Rotator(
      currentAzimuth,
      limitCW,
      limitCCW,
      configureation,
      movingx,
      offsetx,
      targetAzimuthx,
      startAzimuthx,
      outOfLimits,
      name
    )
  }
}

