package uk.ac.aber.adk15.controllers.ui

import java.io.{File, PrintWriter, StringWriter}
import javafx.application.Platform

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.controllers.ApplicationController
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.model.Config.Constants.DefaultConfig
import uk.ac.aber.adk15.view.{ApplicationView, ConfigurationView, ProgressPanel}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, Label}
import scalafx.scene.layout.AnchorPane
import scalafx.stage.FileChooser
import scalafxml.core.macros.sfxml

@sfxml
class ApplicationViewController(private val mainController: ApplicationController,
                                private val loadedCreasePatternLabel: Label,
                                private val progressAnchor: AnchorPane,
                                private val progressPanel: ProgressPanel,
                                private val startButton: Button) {

  private val logger = Logger[ApplicationViewController]

  private var creasePatternFile: Option[File] = None
  private var currentConfig: Config           = DefaultConfig

  def start(): Unit = {
    implicit val executionContext =
      ExecutionContext.fromExecutor((command: Runnable) => Platform.runLater(command))

    startButton.disable = true
    progressAnchor.children = progressPanel

    if (creasePatternFile.isDefined) {
      mainController.execute(creasePatternFile.get, currentConfig) onComplete {
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
    currentConfig = ConfigurationView.showConfigDialog(default = currentConfig)
  }

  def loadCreasePattern(): Unit = {
    val fileChooser = new FileChooser
    creasePatternFile = Option(fileChooser.showOpenDialog(ApplicationView))
    loadedCreasePatternLabel.text = creasePatternFile map (_.getName) getOrElse "Error Loading crease file"
  }

  private def showExceptionMessage(ex: Throwable): Unit = {
    new Alert(AlertType.Error) {
      title = "An exception occurred"
      headerText = s"${ex.getClass.getSimpleName}"
      contentText = {
        val sw = new StringWriter()
        val pw = new PrintWriter(sw)
        ex.printStackTrace(pw)
        sw.toString
      }

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
