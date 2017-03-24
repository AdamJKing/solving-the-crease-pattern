package uk.ac.aber.adk15.controllers.ui

import uk.ac.aber.adk15.controllers.ConfigurationController
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.view.ConfigurationView

import scalafx.scene.control.Spinner
import scalafxml.core.macros.sfxml

@sfxml
class ConfigurationViewController(private val maxThreads: Spinner[Int],
                                  private val configurationController: ConfigurationController) {

  def configureApplication(): Unit = {
    configurationController configureApplication
      Config(maxThreads.getValue)

    ConfigurationView.hide()
  }

  def cancelConfiguration(): Unit = {
    ConfigurationView.hide()
  }
}
