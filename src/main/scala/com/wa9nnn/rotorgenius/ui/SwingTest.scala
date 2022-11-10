package com.wa9nnn.rotorgenius.ui

import com.wa9nnn.rotorgenius.RotatorInterface
import com.wa9nnn.rotorgenius.arco.Headerlistener
import com.wa9nnn.rotorgenius.rg.{RGHeader, Rotator}
import org.jfree.data.general.DefaultValueDataset

import javax.swing.SwingUtilities
import scala.swing._


//noinspection ZeroIndexToHead
class SwingTest(rotatorInterface: RotatorInterface) extends Headerlistener {

  rotatorInterface.addListener(this)
  private val rotatorPanels = Seq(new RotatorPanel(), new RotatorPanel())

  private val rotatorA: Table = new Table(new Rotator())

  private val rotatorB: Table = new Table(new Rotator())
  val compassDataSetA = new DefaultValueDataset(1)
  val compassDataSetB = new DefaultValueDataset(2)

  //  private val column: TableColumn = rotatorB.peer.getColumnModel.getColumn(0)
  //  column.setWidth(200)
  //  column.setPreferredWidth(250)
  private val f: MainFrame = new MainFrame {
    title = "Rotator Genius"

    menuBar = new MenuBar {
      contents += new Menu("Debug") {
        contents += new MenuItem(Action("Raw Data") {
          println("Action '" + title + "' invoked")
          val dialog: Dialog = new Dialog(f) {
            size = new Dimension(500, 400)
            title = "Raw Data from Rotator Controller"
            modal = false
            val boxPanel = new BoxPanel(Orientation.Horizontal) {
              contents += rotatorA
              contents += rotatorB
            }
            contents = boxPanel
          }
          dialog.open()
        })
      }
    }

    private val boxPanel = new BoxPanel(Orientation.Horizontal) {
      rotatorPanels.foreach { rp =>
        contents += rp
      }
    }

    contents = boxPanel

    size = new Dimension(600, 300)
    centerOnScreen
  }
  f.visible = true

  override def newHeader(header: RGHeader): Unit = {
    //noinspection ZeroIndexToHead
    SwingUtilities.invokeLater(() => {
      header.rotators.zipWithIndex.foreach { case (rotator, index) =>
        rotatorPanels(index)(rotator)
      }
    })
  }
}
