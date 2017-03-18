package uk.ac.aber.adk15.controllers.ui

import uk.ac.aber.adk15.controllers.ApplicationController
import uk.ac.aber.adk15.view.ConfigurationView

import scalafxml.core.macros.sfxml

@sfxml
class ApplicationViewController(private val mainController: ApplicationController) {

  def start(): Unit = mainController.start()

  def configure(): Unit = {
    ConfigurationView.show()
  }

  def loadCreasePattern(): Unit = {}
}
