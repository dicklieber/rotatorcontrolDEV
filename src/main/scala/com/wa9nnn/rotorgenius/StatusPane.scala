package com.wa9nnn.rotorgenius

import com.typesafe.scalalogging.LazyLogging
import org.scalafx.extras.onFX
import scalafx.beans.property.StringProperty
import scalafx.scene.layout.{BorderPane, HBox, VBox}

import java.util.concurrent.atomic.AtomicInteger

class StatusPane extends BorderPane with LazyLogging {
  private val topPane: TopPane = new TopPane()
  //  private val rotator1: RotatorPane = new RotatorPane()
  //  private val rotator2: RotatorPane = new RotatorPane()
  //  center =  new HBox(2.0, topPane, rotator1, rotator2)
  center = topPane
  //  right = rotator1
  //  left = rotator2

  def update(header: RGHeader): Unit = {
    topPane.update(header)
    //    rotator1.update(header.rotator1)
    //    rotator2.update(header.rotator2)
  }
}

class TopPane extends GridOfControls {
  styleClass.add("topPane")

  private val stamp = add("stamp", "...")
  private val panic = add("panic", "...")

  val r1 = new RotatorDataData(this)
  val r2 = new RotatorDataData(this)

  def update(header: RGHeader): Unit = {
    onFX {
      set(stamp, header.stamp.toString)
      set(panic, header.panic)

      r1.update(header.rotator1)
      r2.update(header.rotator2)
    }
  }

}

class RotatorDataData(grid: GridOfControls) {
  val rpCountSouexe = new AtomicInteger()

  private val rpcount = grid.add("rpcount", "...")
  private val currentAzimuth = grid.add("CurrentAzimuth", "...")
  private val limitCW = grid.add("LimitCW", "...")
  private val limitCCW = grid.add("LimitCCW", "...")
  private val configuration = grid.add("Configuration", "...")
  private val moving = grid.add("Moving", "...")
  private val offsetx = grid.add("Offsetx", "...")
  private val targetAzimuth = grid.add("TargetAzimuth", "...")
  private val startAzimuth = grid.add("StartAzimuth", "...")
  private val outOfLimits = grid.add("OutOfLimits", "...")
  private val name = grid.add("Name", "...")

  def update(rotator: Rotator): Unit = {
      set(rpcount, rpCountSouexe.getAndIncrement())
      set(currentAzimuth, rotator.currentAzimuth)
      set(limitCW, rotator.limitCW)
      set(limitCCW, rotator.limitCCW)
      set(configuration, rotator.configureation)
      set(moving, rotator.moving)
      set(offsetx, rotator.offsetx)
      set(targetAzimuth, rotator.targetAzimuth)
      set(startAzimuth, rotator.startAzimuth)
      set(outOfLimits, rotator.outOfLimits)
      set(name, rotator.name)
  }

  def set(p: StringProperty, value: Any): Unit = {
    val cell = com.wa9nnn.util.tableui.Cell(value)
    p.value = cell.value
    p
  }
}
