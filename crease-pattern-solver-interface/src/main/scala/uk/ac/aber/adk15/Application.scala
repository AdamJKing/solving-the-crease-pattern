package uk.ac.aber.adk15

import java.io.IOException

import com.google.inject.{Guice, Injector}
import uk.ac.aber.adk15.view.ApplicationView

import scala.util.{Failure, Success, Try}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafxml.core.{ControllerDependencyResolver, FXMLView}
import scalafxml.guice.GuiceDependencyResolver

object Application extends JFXApp {

  implicit val injector: Injector = Guice createInjector new ApplicationModule
  implicit val dependencyResolver = new GuiceDependencyResolver()

  ApplicationView.scene = loadFromFxml("application_skeleton.fxml")

  private def loadFromFxml(filename: String)(
      implicit dependencyResolver: ControllerDependencyResolver) = {
    Try(this.getClass.getClassLoader getResource filename) match {
      case Success(res) =>
        new Scene(FXMLView(res, dependencyResolver))

      case Failure(e) =>
        throw new IOException(s"ERROR: Could not load configuration_panel.fxml", e)
    }
  }
}
