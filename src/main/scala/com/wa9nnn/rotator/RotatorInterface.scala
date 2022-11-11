package com.wa9nnn.rotator

import com.wa9nnn.rotator.arco.Headerlistener
import com.wa9nnn.rotator.rg.ResponseParser.Degree

import scala.util.Try

trait RotatorInterface {
  def addListener(headerlistener: Headerlistener): Unit
  def getPosition: Option[Degree]
  def move(move: Degree): Unit
}
