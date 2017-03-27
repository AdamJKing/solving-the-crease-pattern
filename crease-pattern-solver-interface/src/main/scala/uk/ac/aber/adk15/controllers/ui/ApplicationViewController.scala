package uk.ac.aber.adk15.controllers.ui

import java.io.File
import javafx.application.Platform

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.controllers.ApplicationController
import uk.ac.aber.adk15.view.{ApplicationView, ConfigurationView}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.FileChooser
import scalafxml.core.macros.sfxml

@sfxml
class ApplicationViewController(private val mainController: ApplicationController) {

  private val logger = Logger[ApplicationViewController]

  private var creasePatternFile: Option[File] = None

  def start(): Unit = {
    implicit val executionContext =
      ExecutionContext.fromExecutor((command: Runnable) => Platform.runLater(command))

    if (creasePatternFile.isDefined) {
      mainController.execute(creasePatternFile.get) onComplete {
        case Success(Some(result)) => // show result view
        case Success(None)         => showNoFoldOrderFoundMessage()
        case Failure(ex)           => showExceptionMessage(ex)
      }
    } else {
      logger info "No crease pattern was loaded."
      showNoCreasePatternLoadedMessage()
    }
  }

  def configure(): Unit = {
    ConfigurationView.show()
  }

  def loadCreasePattern(): Unit = {
    val fileChooser = new FileChooser
    creasePatternFile = Option(fileChooser.showOpenDialog(ApplicationView))
  }

  private def showExceptionMessage(ex: Throwable): Unit = {
    new Alert(AlertType.Error) {
      title = "An exception occurred"
      headerText = s"${ex.getClass.getSimpleName}"
      contentText = ex.getMessage

    } showAndWait
  }

  private def showNoFoldOrderFoundMessage(): Unit = {
    new Alert(AlertType.Warning) {
      title = "No valid fold order"
      contentText = "Could not locate a valid fold order. Was the crease pattern legal?"

    } showAndWait
  }

  private def showNoCreasePatternLoadedMessage(): Unit = {
    new Alert(AlertType.Warning) {
      title = "No crease pattern found."
      contentText = "Please load a valid crease pattern (None currently loaded)"

    } showAndWait
  }
}
