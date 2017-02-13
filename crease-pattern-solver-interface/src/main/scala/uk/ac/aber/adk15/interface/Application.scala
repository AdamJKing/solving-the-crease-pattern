package uk.ac.aber.adk15.interface

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, FXMLLoader, Initializable, JavaFXBuilderFactory}
import javafx.scene.{Parent, Scene}
import javafx.scene.layout.StackPane
import javafx.stage.Stage

object Application {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[Application], args: _*)
  }

}

class Application extends javafx.application.Application {

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("HelloWorld")
    val root = new StackPane()
    primaryStage.setScene(new Scene(root))
    val page = FXMLLoader.load(classOf[Application].getResource("/skel.fxml"), null, new JavaFXBuilderFactory()).asInstanceOf[Parent]
    primaryStage.getScene.setRoot(page)
    primaryStage.sizeToScene()
    primaryStage.show()
  }

}

class ApplicationController extends Initializable {

  @FXML def doSomething(): Unit = {
    println("hello world")
  }
  
  override def initialize(url: URL, rb: ResourceBundle): Unit = {
  }

}
