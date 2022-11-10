package com.wa9nnn.rotorgenius

import com.wa9nnn.rotorgenius.arco.ArcoInterface
import com.wa9nnn.rotorgenius.ui.SwingTest


class Server(commandLine: CommandLine) {


  private val rotatorInterface: RotatorInterface = new ArcoInterface(commandLine)
//  private val rotatorInterface = new RotatorGeniusInterface(commandLine)
  new SwingTest(rotatorInterface)

  private val rotctldThread = new Thread("rotctld") {
    override def run(): Unit = {
      new RotctldServer(commandLine, rotatorInterface)
    }
  }
  rotctldThread.setDaemon(true)
  rotctldThread.start()

}
