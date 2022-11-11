package com.wa9nnn.rotator

import com.wa9nnn.rotator.arco.ArcoInterface
import com.wa9nnn.util.HostAndPort


class Server(config: Config) {


  private val rotatorInterface: RotatorInterface = new ArcoInterface(RotatorConfig("default", HostAndPort("192.168.0.123", 4001)))
//  private val rotatorInterface = new RotatorGeniusInterface(commandLine)
//  new SwingTest(rotatorInterface)

  private val rotctldThread = new Thread("rotctld") {
    override def run(): Unit = {
      new RotctldServer(config.rgctldPort, rotatorInterface)
    }
  }
  rotctldThread.setDaemon(true)
  rotctldThread.start()

}
