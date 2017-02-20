package uk.ac.aber.adk15.ui

import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.fxml.{FXML, FXMLLoader, Initializable, JavaFXBuilderFactory}
import javafx.scene.{Parent, Scene}
import javafx.scene.layout.StackPane
import javafx.stage.Stage

object App {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[App], args: _*)
  }

}

class App extends javafx.application.Application {

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("HelloWorld")
    val root = new StackPane()
    primaryStage.setScene(new Scene(root))
    val page = FXMLLoader.load(classOf[App].getResource("/skel.fxml"), null, new JavaFXBuilderFactory()).asInstanceOf[Parent]
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
