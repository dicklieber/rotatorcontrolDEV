package com.wa9nnn.rotorgenius

import com.wa9nnn.rotorgenius.rg.RotatorGeniusInterface
import com.wa9nnn.rotorgenius.ui.SwingTest


class Server(commandLine: CommandLine) {


  private val deviceEngine = new RotatorGeniusInterface(commandLine)
  new SwingTest(deviceEngine)

  private val rotctldThread = new Thread("rotctld") {
    override def run(): Unit = {
      new RotctldServer(commandLine, deviceEngine)
    }
  }
  rotctldThread.setDaemon(true)
  rotctldThread.start()

}
