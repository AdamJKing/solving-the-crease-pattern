package uk.ac.aber.adk15.view

import java.io.IOException

import com.google.inject.Guice
import uk.ac.aber.adk15.ApplicationModule

import scala.util.{Failure, Success, Try}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.FXMLView
import scalafxml.guice.GuiceDependencyResolver

object ApplicationView extends JFXApp {
  implicit val injector = Guice createInjector new ApplicationModule

  stage = new PrimaryStage {

    title.value = "Crease Pattern Solver"

    scene = Try(classOf[App] getResource "/application_skeleton.fxml") match {
      case Success(res) => new Scene(FXMLView(res, new GuiceDependencyResolver()))
      case Failure(e) =>
        throw new IOException("ERROR: Could not load application_skeleton.fxml", e)
    }
  }
}
