package uk.ac.aber.adk15.controllers.ui

import java.io.{File, PrintWriter, StringWriter}
import javafx.application.Platform

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.controllers.ApplicationController
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.model.Config.Constants.DefaultConfig
import uk.ac.aber.adk15.view.ConfigurationView.showConfigDialog
import uk.ac.aber.adk15.view.{ApplicationView, ProgressPane, ResultsView}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Label}
import scalafx.scene.layout.{HBox, Pane, Priority}
import scalafx.stage.FileChooser
import scalafxml.core.macros.sfxml

/**
  * This controller is in charge of separating the interaction with the User Interface code
  * from the rest of the application. As this class cannot be tested manually (see below) any code
  * in this controller should be UI '''only''' and should not need testing through traditional means.
  *
  * @constructor '''Never''' construct manually as this object is transformed by the [[sfxml]]
  *              annotation and therefore will ''not'' exist at runtime.
  *
  * All params for this class are injected by the ScalaFX framework.
  *
  * @param container the central pane defined in the FXML that contains all elements
  * @param mainController the true application controller which interacts with the core classes
  * @param loadedCreasePatternLabel a label which is used to indicate the filename of the file
  *                                 that is loaded
  * @param progressPane our main display area for displaying the progress of the application
  * @param menu the menu bar that contains all the buttons used to manipulate our application
  */
@sfxml
class ApplicationViewController(private val mainController: ApplicationController,
                                private val container: HBox,
                                private val loadedCreasePatternLabel: Label,
                                private val progressPane: ProgressPane,
                                private val menu: Pane) {
  private val logger = Logger[ApplicationViewController]

  private var creasePatternFile: Option[File] = None
  private var currentConfig: Config           = DefaultConfig

  /**
    * The entry point for the execution of the application.
    *
    * @usecase Controller function called by FXML resource.
    */
  def start(): Unit = {
    implicit val executionContext =
      ExecutionContext.fromExecutor((command: Runnable) => Platform.runLater(command))

    disableAll(menu)

    // ask the container to stretch our progress pane to the full available width
    // as it doesn't yet contain any content (and therefore won't scale automatically)
    HBox.setHgrow(progressPane, Priority.Always)

    container.children add progressPane

    creasePatternFile match {
      case Some(creasePattern) =>
        mainController.execute(creasePattern, currentConfig) onComplete {
          case Failure(ex) =>
            logger error s"Exception during execution; exception=$ex"
            showExceptionMessage(ex)

          case Success(result) =>
            logger info s"result=$result"
            handleOutcome(result)
        }
      case None =>
        logger info "No crease pattern was loaded."
        showNoCreasePatternLoadedMessage()
    }
  }

  /**
    * Launches the window through which the application is configured by the user.
    *
    * @usecase Controller function called by FXML resource
    */
  def configure(): Unit = currentConfig = showConfigDialog(default = currentConfig)

  /**
    * Loads a crease pattern selected from the file system as the chosen crease pattern.
    *
    * @usecase Controller function called by FXML resource.
    */
  def loadCreasePattern(): Unit = {
    val fileChooser = new FileChooser
    val file        = fileChooser showOpenDialog (ownerWindow = ApplicationView)

    creasePatternFile = Option(file)

    val fileLabel = creasePatternFile map (_.getName)
    loadedCreasePatternLabel.text = fileLabel getOrElse "Error loading crease file"
  }

  /**
    * Process the outcome of an [[ExecutionResult]]. Displays either a [[ResultsView]] or
    * a [[Alert]] depending on the result.
    *
    * @param outcome the result returned from the execution function.
    */
  private def handleOutcome(outcome: ExecutionResult): Unit = outcome match {
    case SuccessfulExecution(result, creasePattern) =>
      new ResultsView(result, creasePattern).showAndWait()

    case FailedExecution() => showNoFoldOrderFoundMessage()
  }

  /**
    * Disables all the elements in the given pane.
    *
    * @param pane the element containing the children to be disabled
    */
  def disableAll(pane: Pane): Unit = pane.children foreach (_.disable = true)

  def showExceptionMessage(ex: Throwable): Unit = {
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

  def showNoFoldOrderFoundMessage(): Unit = {
    new Alert(AlertType.Warning) {
      title = "No valid fold order"
      contentText = "Could not locate a valid fold order. Was the crease pattern legal?"

    } showAndWait
  }

  def showNoCreasePatternLoadedMessage(): Unit = {
    new Alert(AlertType.Warning) {
      title = "No crease pattern found."
      contentText = "Please load a valid crease pattern (None currently loaded)"

    } showAndWait
  }
}
