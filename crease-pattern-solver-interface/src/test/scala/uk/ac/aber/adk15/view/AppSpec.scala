package uk.ac.aber.adk15.view

import org.scalatest.{FlatSpec, Matchers}
import uk.ac.aber.adk15.Application

import scalafx.application.Platform

class ApplicationSpec extends FlatSpec with Matchers {

  "The Application" should "start successfully and run for 30 seconds" in {
    new Thread(() => Application.main(Array()))
    Thread.sleep(30000L)
    Platform.exit()
  }
}
