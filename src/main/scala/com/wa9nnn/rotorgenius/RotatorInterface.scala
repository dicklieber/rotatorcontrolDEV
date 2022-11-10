package com.wa9nnn.rotorgenius

import com.wa9nnn.rotorgenius.arco.Headerlistener
import com.wa9nnn.rotorgenius.rg.ResponseParser.Degree

import scala.util.Try

trait RotatorInterface {
  def addListener(headerlistener: Headerlistener): Unit
  def getPosition: Option[Degree]
  def move(move: Degree): Unit
}
