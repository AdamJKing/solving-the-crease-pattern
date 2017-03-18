package uk.ac.aber.adk15

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import uk.ac.aber.adk15.controllers.{
  ApplicationController,
  ApplicationControllerImpl,
  ConfigurationController,
  ConfigurationControllerImpl
}
import uk.ac.aber.adk15.executors.{FoldExecutorFactory, FoldExecutorFactoryImpl}
import uk.ac.aber.adk15.model.{ConfigurationService, ConfigurationServiceImpl}

class ApplicationModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[ConfigurationService].to[ConfigurationServiceImpl]
    bind[FoldExecutorFactory].to[FoldExecutorFactoryImpl]
    bind[ApplicationController].to[ApplicationControllerImpl]
    bind[ConfigurationController].to[ConfigurationControllerImpl]
  }
}
