package uk.ac.aber.adk15.view

import uk.ac.aber.adk15.model.Config

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.GridPane

object ConfigurationView {

  def showConfigDialog(default: Config = Config.Constants.DefaultConfig): Config = {

    val dialog = new Dialog[Option[Config]]() {
      initOwner(ApplicationView)
      title = "Configuration"
    }

    // Set the button types.
    dialog.dialogPane().buttonTypes = List(ButtonType.Apply, ButtonType.Cancel)

    // Create the username and password labels and fields.
    val maxThreadsSpinner = new Spinner[Int](1, 256, default.maxThreads)

    val grid = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label("Max. Threads"), 0, 0)
      add(maxThreadsSpinner, 1, 0)
    }

    dialog.dialogPane().content = grid

    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.Apply) Option(Config(maxThreadsSpinner.getValue))
      else None

    val result = dialog.showAndWait()

    //
    // This code is a little bit bizarre, on the surface it seems to only return
    // the exact same thing as `result`.
    // In actual fact it's allowing ScalaFX to do some strange type magic and frankly
    // won't compile if we don't do this.
    //
    result match {
      case Some(Some(config: Config)) => config
      case Some(_)                    => default
      case unexpected =>
        throw new IllegalStateException(s"${unexpected.getClass.getSimpleName} was unexpected")
    }
  }
}
