package com.wa9nnn.rotator.ui

import scalafx.scene.control.{TextField, TextFormatter, TextInputControl}
import scalafx.scene.input.KeyEvent
import scalafx.util.converter.FormatStringConverter

import java.text.NumberFormat
import scala.util.matching.Regex
import _root_.scalafx.Includes._

object InputHelper {
  /**
   *
   * @param textFields that will ensure uppercase
   */
  def forceCaps(textFields: TextInputControl*): Unit = {
    textFields.foreach {
      _.setTextFormatter(new TextFormatter[AnyRef]((change: TextFormatter.Change) => {
        def foo(change: TextFormatter.Change) = {
          change.setText(change.getText.toUpperCase)
          change
        }

        foo(change)
      }))
    }
  }

  /**
   *
   * @param textField to work with.
   * @param regex     discard Chars not passing this.
   */
  def forceAllowed(textField: TextInputControl, regex: Regex): Unit = {
    textField.onKeyPressed = { event: KeyEvent =>
      Option(event.text).foreach { ch: String =>
        val matches = regex.matches(ch)
        if (!matches) {
          event.consume()
        }
      }
    }
  }

  /**
   *
   * @param textFields that will ensure integer only
   */
  def forceInt(textFields: TextField*): Unit = {
    val nf: NumberFormat = NumberFormat.getIntegerInstance()
    val converter: FormatStringConverter[Number] = new FormatStringConverter[Number](nf)

    textFields.foreach { tf =>
      tf.setTextFormatter(new TextFormatter(converter))
    }
  }


}
