package com.wa9nnn.rotator.ui


object SystemPropAbout {


  def apply(goc:GridOfControls):Unit = {
    def addFilePath(key:String):Unit = {
      goc.addLabel(key, System.getProperty(key))
    }
//    def addFilePath(key:String):Unit = {
//      goc.adddMultiLine(key, System.getProperty(key))
//    }


//    goc.addLabel()
  }
}
