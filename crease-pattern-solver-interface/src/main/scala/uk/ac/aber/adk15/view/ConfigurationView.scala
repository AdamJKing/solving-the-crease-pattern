package uk.ac.aber.adk15.view

import java.io.IOException

import com.google.inject.Guice
import uk.ac.aber.adk15.ApplicationModule

import scala.util.{Failure, Success, Try}
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.stage.{Modality, Stage}
import scalafxml.core.FXMLView
import scalafxml.guice.GuiceDependencyResolver

object ConfigurationView extends Stage {
  private implicit val injector = Guice createInjector new ApplicationModule

  initModality(Modality.ApplicationModal)

  title = "Configuration"

  scene = Try(classOf[App] getResource "/configuration_panel.fxml") match {
    case Success(res) => new Scene(FXMLView(res, new GuiceDependencyResolver()))
    case Failure(e) =>
      throw new IOException(s"ERROR: Could not load configuration_panel.fxml", e)
  }

  hide()
}
