package com.wa9nnn.rotorgenius

import com.typesafe.scalalogging.LazyLogging
import org.scalafx.extras.onFX
import scalafx.scene.layout.{BorderPane, HBox}

import java.util.concurrent.atomic.AtomicInteger

class StatusPane extends BorderPane with LazyLogging {
  private val topPane: TopPane = new TopPane()
  top = topPane

  private val rotator1 = new RotatorPane()
  private val rotator2 = new RotatorPane()
  center = new HBox(rotator1, rotator2)

  def update(header: RGHeader): Unit = {
    topPane.update(header)
    rotator1.update(header.rotator1)
    rotator2.update(header.rotator2)
  }
}

class TopPane extends GridOfControls {
  styleClass.add("topPane")

  private var counterSource = new AtomicInteger()
  private val stamp = add("stamp", "...")
  private val counter = add("counter", "...")
  private val panic = add("panic", "...")
  private val currentAzimuth = add("CurrentAzimuth", "...")


  def update(header: RGHeader): Unit = {
    onFX {
      set(counter, counterSource.incrementAndGet())
      set(stamp, header.stamp.toString)
      set(panic, header.panic)
      set(currentAzimuth, header.rotator1.currentAzimuth)
    }
  }
}

class RotatorPane extends GridOfControls {
  styleClass.add("rotatorPane")
  val rpCountSouexe = new AtomicInteger()

  private val rpcount = add("rpcount", "...")
  private val currentAzimuth = add("CurrentAzimuth", "...")
  private val limitCW = add("LimitCW", "...")
  private val limitCCW = add("LimitCCW", "...")
  private val configuration = add("Configuration", "...")
  private val moving = add("Moving", "...")
  private val offsetx = add("Offsetx", "...")
  private val targetAzimuth = add("TargetAzimuth", "...")
  private val startAzimuth = add("StartAzimuth", "...")
  private val outOfLimits = add("OutOfLimits", "...")
  private val name = add("Name", "...")

  def update(rotator: Rotator): Unit = {
    onFX {
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
  }
}
