package com.wa9nnn.rotorgenius

import com.wa9nnn.rotorgenius.rg.RotatorGeniusInterface


class Server(commandLine: CommandLine) {

    private val deviceEngine = new RotatorGeniusInterface(commandLine)

    private val rotctldThread = new Thread("rotctld") {
      override def run(): Unit = {
        new RotctldServer(commandLine, deviceEngine)
      }
    }
    rotctldThread.setDaemon(true)
    rotctldThread.start()

}
