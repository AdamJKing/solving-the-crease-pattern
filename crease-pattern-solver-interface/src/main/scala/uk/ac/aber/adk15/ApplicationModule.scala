package uk.ac.aber.adk15

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import uk.ac.aber.adk15.controllers._
import uk.ac.aber.adk15.executors.ant._
import uk.ac.aber.adk15.services.{FoldSelectionService, FoldSelectionServiceImpl}
import uk.ac.aber.adk15.view.ProgressPanel

class ApplicationModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[AntBasedFoldExecutor].to[AntBasedFoldExecutorImpl]
    bind[AntTraverser].to[AntTraverserImpl]
    bind[ApplicationController].to[ApplicationControllerImpl]
    bind[CreasePatternParser].to[CreasePatternParserImpl]
    bind[DiceRollService].to[DiceRollServiceImpl]
    bind[FoldSelectionService].to[FoldSelectionServiceImpl]
    bind[ProgressPanel]
  }
}
