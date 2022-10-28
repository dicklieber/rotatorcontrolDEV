package com.wa9nnn.rotorgenius


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
