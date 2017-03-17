package uk.ac.aber.adk15.view

import scalafx.stage.{Modality, Stage}

object ConfigurationView extends Stage {
  initModality(Modality.ApplicationModal)

  title = "Configuration"

  hide()
}
