package com.wa9nnn.rotorgenius

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.util.HostAndPort
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Tab, TabPane}

object App extends JFXApp3 with LazyLogging {

  //  private val mcHostAndPort = HostAndPort(Option(System.getProperty("multicastgroup")).getOrElse("239.73.88.0:1174"), 1174)
  //  val multicastAddress: InetAddress = InetAddress.getByName(mcHostAndPort.host)



  override def stopApp(): Unit = {
    System.exit(0)
  }

  override def start(): Unit = {
//    val multicastPane = new BorderPane {
//      top = new Label("Multicast Stats Pane")
//      center = new TableView[NodeStats](multicast.nodes) {
//        columns ++= List(
//          new TableColumn[NodeStats, String] {
//            text = "Host"
//            cellValueFactory = _.value.host
//            prefWidth = 180
//          },
//          new TableColumn[NodeStats, Long] {
//            text = "Count"
//            cellValueFactory = _.value.totalReceived
//            prefWidth = 50
//          },
//          new TableColumn[NodeStats, Long] {
//            text = "S/N"
//            cellValueFactory = _.value.lastSn
//            prefWidth = 50
//          },
//          new TableColumn[NodeStats, String] {
//            text = "Since"
//            cellValueFactory = _.value.since
//            prefWidth = 100
//          }
//        )
//      }
//    }

//    val statusPane = new StatusPane()
//    val messagesPane = new MessagesPane

//    val multicastTab = new Tab {
//      text = "Multicast Data"
//      closable = false
//      content = multicastPane
//    }
//
//    val wsjtTab = new Tab {
//      text = "WSJT"
//      closable = false
//      content = new HBox(statusPane, messagesPane)
//    }

    stage = new JFXApp3.PrimaryStage {
      title = s"Rotor Genius"
      width = 1200
      height = 800
      private val statusTab = new Tab {
        text = "Status"
        closable = false
        private val statusPane = new StatusPane
        new DeviceEngine(statusPane)
        content = statusPane
      }


      scene = new Scene {
        val cssUrl: String = getClass.getResource("/scalafx.css").toExternalForm
        stylesheets.setAll( cssUrl)

        content = new TabPane {
          tabs = Seq(statusTab)
        }
      }
    }
  }



  def hostAndPort(propName: String, default: String, defaultPort: Int): HostAndPort = {
    val ret = HostAndPort(Option(System.getProperty(propName)).getOrElse(default), defaultPort)
    logger.info(s"{}: {}", propName, ret)
    ret
  }

}
