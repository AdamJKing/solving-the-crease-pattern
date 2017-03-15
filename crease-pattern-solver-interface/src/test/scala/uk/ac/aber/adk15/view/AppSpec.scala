package uk.ac.aber.adk15.view

import org.scalatest.{FlatSpec, Matchers}

import scalafx.application.Platform

/**
  * Created by adam on 13/02/17.
  */
class ApplicationSpec extends FlatSpec with Matchers {

  "The Application" should "start successfully and run for 30 seconds" in {
    new Thread(() => ApplicationView.main(Array()))
    Thread.sleep(30000L)
    Platform.exit()
  }
}
