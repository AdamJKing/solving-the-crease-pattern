package uk.ac.aber.adk15

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import uk.ac.aber.adk15.controllers.{
  ApplicationController,
  ApplicationControllerImpl,
  ConfigurationController,
  ConfigurationControllerImpl
}
import uk.ac.aber.adk15.executors.ant.{AntBasedFoldExecutor, AntBasedFoldExecutorImpl}
import uk.ac.aber.adk15.model.{ConfigurationService, ConfigurationServiceImpl}
import uk.ac.aber.adk15.services.{FoldSelectionService, FoldSelectionServiceImpl}

class ApplicationModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[ConfigurationService].to[ConfigurationServiceImpl]
    bind[ApplicationController].to[ApplicationControllerImpl]
    bind[ConfigurationController].to[ConfigurationControllerImpl]
    bind[FoldSelectionService].to[FoldSelectionServiceImpl]
    bind[AntBasedFoldExecutor].to[AntBasedFoldExecutorImpl]
  }
}
