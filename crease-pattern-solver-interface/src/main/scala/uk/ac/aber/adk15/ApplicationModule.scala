package uk.ac.aber.adk15

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import uk.ac.aber.adk15.controllers._
import uk.ac.aber.adk15.executors.ant._
import uk.ac.aber.adk15.view.{EventBus, ProgressPane}

/**
  * Guice module that defines the implementations for our interfaces.
  *
  */
class ApplicationModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[AntBasedFoldExecutor].to[AntBasedFoldExecutorImpl]
    bind[AntTraverser].to[AntTraverserImpl]
    bind[ApplicationController].to[ApplicationControllerImpl]
    bind[CreasePatternParser].to[CreasePatternParserImpl]
    bind[DiceRollService].to[DiceRollServiceImpl]
    bind[EventBus[AntTraversalEvent]] toInstance new EventBus[AntTraversalEvent]
    bind[ProgressPane]
  }
}
