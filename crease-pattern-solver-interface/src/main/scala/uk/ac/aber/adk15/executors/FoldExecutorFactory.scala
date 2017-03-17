package uk.ac.aber.adk15.executors

import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.model.ConfigurationConstants.ExecutorType._

trait FoldExecutorFactory {
  def createFactoryFrom(config: Config): FoldExecutor
}

class FoldExecutorFactoryImpl extends FoldExecutorFactory {
  def createFactoryFrom(config: Config): FoldExecutor = {
    config.foldFinderType match {
      case PreDesignated => new PreDesignatedFoldExecutor
    }
  }
}
