package uk.ac.aber.adk15.controllers.ui

import java.io.File
import java.util.concurrent.ForkJoinPool

import uk.ac.aber.adk15.controllers.ApplicationController
import uk.ac.aber.adk15.view.{ApplicationView, ConfigurationView}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.FileChooser
import scalafxml.core.macros.sfxml

@sfxml
class ApplicationViewController(private val mainController: ApplicationController, private val creasePatternParser: CreasePatternParser) {

  private var creasePatternFile: Option[File] = None

  def start(): Unit = {
    implicit val executionContext =
      ExecutionContext.fromExecutor(new ForkJoinPool(8))

    val creasePattern = creasePatternFile map creasePatternParser.parse(_)

    mainController.execute(creasePattern) onComplete {
      case Success(Some(result)) => // show result view
      case Success(None)         => // show condolence view
      case Failure(ex) =>
        new Alert(AlertType.Error) {
          title = "An exception occurred"
          headerText = s"${ex.getClass.getSimpleName}"
          contentText = ex.getMessage
        }
    }
  }

  def configure(): Unit = {
    ConfigurationView.show()
  }

  def loadCreasePattern(): Unit = {
    val fileChooser = new FileChooser
    creasePatternFile = Option(fileChooser.showOpenDialog(ApplicationView))
  }
}
