package com.wa9nnn.rotator.rg

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.rotator.rg.Configuration.Configuration
import com.wa9nnn.rotator.rg.Moving.Moving
import com.wa9nnn.rotator.rg.RGHeader.rowCount
import com.wa9nnn.rotator.rg.ResponseParser.{Degree, Offset}
import com.wa9nnn.util.Stamped
import com.wa9nnn.util.tableui.Cell

import javax.swing.table.AbstractTableModel
import scala.swing.Table

/**
 * Internalized version of message returned via the "|h" request.
 * based on "4O3A RotorGenius_Protocol_Description_rev4.pdf"
 *
 * @param panic 0 if ok, otherwise undefined in
 */
case class RGHeader(
                     panic: Int = 0,
                     rotators: List[Rotator] = List.empty
                   ) extends LazyLogging with Stamped {
}

/**
 * See "4O3A RotorGenius_Protocol_Description_rev4.pdf" section 1.0
 */
case class Rotator(
                    currentAzimuth: Option[Degree] = None,
                    limitCW: Degree = -1,
                    limitCCW: Degree = -1,
                    configuration: Configuration = Configuration.Azimuith,
                    moving: Moving = Moving.Stopped,
                    offsetx: Offset = 0,
                    targetAzimuth: Option[Degree] = None,
                    startAzimuth: Option[Degree] = None,
                    outOfLimits: Boolean = false,
                    name: String = "???"
                  ) extends AbstractTableModel with LazyLogging {



//  def table: Table = {
//    new Table(this) {
//
//    }
//  }

  lazy val c: Seq[(String, Cell)] = contents

  override def getRowCount: Degree = rowCount

  override def getColumnCount: Degree = 2


  override def getColumnName(column: Int): String = {
    column match {
      case 0 => "Name"
      case 1 => "Value"
      //      case x =>
      //        throw new IllegalArgumentException()
    }
  }


  override def getValueAt(rowIndex: Degree, columnIndex: Degree): AnyRef = {
    val row: (String, Cell) = c(rowIndex)
    columnIndex match {
      case 0 =>
        row._1
      case 1 =>
        row._2.toString()
    }
  }


  def contents: Seq[(String, Cell)] = {
    (for {
      i <- 0 until  productArity
    } yield {
      val name = productElementName(i)
      val value = Cell(productElement(i))
      name -> value
    }).sortBy(_._1)
  }


}


object RGHeader {
  val rowCount = 10

  def apply(response: Array[Byte]): RGHeader = {
    implicit val parser = new ResponseParser(response)
    if (parser.nextString(2) != "|h")
      throw new IllegalArgumentException("""Response does not start with "|h"!""")

    parser.nextString(1) //ign ore obsolete active flag
    val panic = parser.nextByte()
    val r1 = Rotator.apply
    val r2 = Rotator.apply
    new RGHeader(panic, List(r1, r2))
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

