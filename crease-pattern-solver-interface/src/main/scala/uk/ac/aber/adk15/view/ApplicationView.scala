package uk.ac.aber.adk15.view

import scalafx.application.JFXApp.PrimaryStage

/**
  * The object representation of the main application view.
  * Pretty thin because the actual content is added by [[uk.ac.aber.adk15.Application]]
  */
object ApplicationView extends PrimaryStage {
  title.value = "Crease Pattern Solver"
}
