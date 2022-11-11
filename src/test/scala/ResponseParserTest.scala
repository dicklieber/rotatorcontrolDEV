import com.wa9nnn.rotator.rg.{RGHeader, ResponseParser}

class ResponseParserTest extends org.specs2.mutable.Specification {
  //  val response = "|h2\u0000999359  0A0   0999999\u0000Tower        44359  0A0   0999999\u0000Rotator 2   "

  val response: Array[Byte] = Array[Byte](124, 104, 50, 0, 57, 57, 57, 51, 53, 57, 32, 32, 48, 65, 48, 32, 32, 32, 48, 57, 57, 57, 57, 57, 57, 0, 84, 111, 119, 101, 114, 32, 32, 32, 32, 32, 32, 32, 32, 52, 51, 51, 53, 57, 32, 32, 48, 65, 48, 32, 32, 32, 48, 57, 57, 57, 57, 57, 57, 0, 82, 111, 116, 97, 116, 111, 114, 32, 50, 32, 32, 32)
  "simple parse" >> {
    val parser = new ResponseParser(response)
    val header = parser.nextString(2)
    header must beEqualTo("|h")

    val active = parser.nextString(1)
    active must beEqualTo("2")

    val panic = parser.nextString(1)
    panic must beEqualTo("")
    val currentAzimuthx = parser.nextString(3)
    currentAzimuthx must beEqualTo("999")

    val limitCWx = parser.nextString(3)
    limitCWx must beEqualTo("359")

    val limitCCWx = parser.nextString(3)
    limitCCWx must beEqualTo("0")

    val Configurationx = parser.nextString(1)
    Configurationx must beEqualTo("A")

    val movingx = parser.nextString(1)
    movingx must beEqualTo("0")

    val offsetx = parser.nextString(4)
    offsetx must beEqualTo("0")

    val targetAzimuthx = parser.nextString(3)
    targetAzimuthx must beEqualTo("999")

    val startAzimuthx = parser.nextString(3)
    startAzimuthx must beEqualTo("999")

    val Limitx = parser.nextString(1)
    //    Limitx must beEqualTo("0") // docs say 0 or 1
    Limitx must beEmpty

    val name = parser.nextString(12)
    name must beEqualTo("Tower")
  }

  "parse header" >> {
    val header: RGHeader = RGHeader(response)
    header.toString must beEqualTo("Header(0,Rotator(None,359,0,Azimuith,Stopped,0,None,None,false,Tower),Rotator(Some(43),359,0,Azimuith,Stopped,0,None,None,false,Rotator 2))")
  }
}