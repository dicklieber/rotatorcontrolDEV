package com.wa9nnn.rotorgenius.rg

import org.specs2.mutable.Specification

class MoveTest extends Specification {
  "toCommand" >> {
    "happy" >> {
      val move = Move(1, 42)
      val command = move.rgCommand
      val str = new String(command)
      str must beEqualTo("|A1042")
    }
    "Out of range degree" >> {
      Move(1, 361) must throwAn[IllegalArgumentException]
    }
    "Out of range rotator" >> {
      Move(0, 123) must throwAn[IllegalArgumentException]
    }
  }
  "parse result" >> {
    "ok" >> {
      Move.checkResult("|AK".getBytes()) must beNone
    }
    "fail" >> {
      Move.checkResult("|AF".getBytes()) must beSome("""Move failed expected "|AK" but got "|AF"""")
    }
//    "crap" >> {
//      Move.checkResult( Array[Byte](3,4)) must beSome("""Move failed expected "|AK" but got " """")
//    }
  }
}