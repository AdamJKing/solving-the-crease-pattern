package uk.ac.aber.adk15.ui

import javafx.application.{Application, Platform}

import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Future


/**
  * Created by adam on 13/02/17.
  */
class AppSpec extends FlatSpec with Matchers {

  "The Application" should "start successfully and run for 30 seconds" in {
    new Thread(() => Application.launch(classOf[App], ""))
    Thread.sleep(30000L)
    Platform.exit()
  }
}
