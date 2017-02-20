package uk.ac.aber.adk15.ui

import java.io.IOException

import scala.util.{Failure, Success, Try}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLView, NoDependencyResolver}

object Application extends JFXApp {
  stage = new PrimaryStage {

    title.value = "Crease Pattern Solver"

    width = 600
    height = 450

    scene = Try(classOf[App] getResource "/application_skeleton.fxml") match {
      case Success(res) => new Scene(FXMLView(res, NoDependencyResolver))
      case Failure(e) => throw new IOException("ERROR: Could not load application_skeleton.fxml", e)
    }
  }
}
