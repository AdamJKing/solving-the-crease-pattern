package uk.ac.aber.adk15.controllers.ui

import uk.ac.aber.adk15.controllers.ConfigurationController
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.model.ConfigurationConstants.ExecutorType
import uk.ac.aber.adk15.model.ConfigurationConstants.ExecutorType.ExecutorType
import uk.ac.aber.adk15.view.ConfigurationView

import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ComboBox, Spinner}
import scalafxml.core.macros.sfxml

@sfxml
class ConfigurationViewController(private val maxThreads: Spinner[Int],
                                  private val executorTypeSelector: ComboBox[ExecutorType],
                                  private val configurationController: ConfigurationController) {

  executorTypeSelector.items set ObservableBuffer(ExecutorType.values toSeq)
  executorTypeSelector.getSelectionModel.selectFirst()

  def configureApplication(): Unit = {
    configurationController configureApplication
      Config(executorTypeSelector.getValue, maxThreads.getValue)

    ConfigurationView.hide()
  }

  def cancelConfiguration(): Unit = {
    ConfigurationView.hide()
  }
}
