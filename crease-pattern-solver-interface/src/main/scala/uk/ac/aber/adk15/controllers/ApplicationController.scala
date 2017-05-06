package uk.ac.aber.adk15.controllers

import java.util.Calendar
import java.util.concurrent.ForkJoinPool

import com.google.inject.Inject
import uk.ac.aber.adk15.executors.ant.AntBasedFoldExecutor
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.fold.Fold

import scala.concurrent.ExecutionContext.fromExecutor
import scala.concurrent.Future

/**
  * The controller that orchestrates the core process of the application.
  * Not to be confused with [[uk.ac.aber.adk15.controllers.ui.ApplicationViewController]]
  * which handles the UI interaction.
  */
trait ApplicationController {
  import ApplicationController._

  def execute(creasePattern: CreasePattern, config: Config): Future[ExecutionResult]
}

/**
  * Implementation of [[ApplicationController]]
  * @param antBasedFoldExecutor the type of executor we want to use to discover the fold-order
  */
class ApplicationControllerImpl @Inject()(private val antBasedFoldExecutor: AntBasedFoldExecutor)
    extends ApplicationController {
  import ApplicationController._

  override def execute(creasePattern: CreasePattern, config: Config): Future[ExecutionResult] = {
    // tell our program not to use more threads than desired by the user
    implicit val executionContext = fromExecutor(new ForkJoinPool(config.maxThreads))

    // start timer
    implicit val startTime = getTimeInMilliseconds

    // find fold-order
    val futureFoldOrder = antBasedFoldExecutor findFoldOrder (creasePattern, config.maxThreads)

    futureFoldOrder map {
      case Some(foldOrder) => SuccessfulExecution(foldOrder, creasePattern)
      case None            => FailedExecution()
    }
  }
}

/**
  * Companion object for [[ApplicationController]]
  */
object ApplicationController {

  def getTimeInMilliseconds: Long = Calendar.getInstance().getTimeInMillis

  /**
    * Holds useful information about the execution context
    *
    * @param startTimeInMilliseconds used to calculate how long the execution took
    */
  abstract class ExecutionResult(startTimeInMilliseconds: Long) {
    val executionTimeInMilliseconds: Long = getTimeInMilliseconds - startTimeInMilliseconds
  }

  /**
    * Represents a successful execution
    *
    * @param foldOrder the discovered fold-order
    * @param extractedCreasePattern the crease-pattern that was examined
    * @param startTimeInMilliseconds used to calculate how long the execution took
    */
  case class SuccessfulExecution(foldOrder: List[Fold], extractedCreasePattern: CreasePattern)(
      implicit val startTimeInMilliseconds: Long)
      extends ExecutionResult(startTimeInMilliseconds)

  /**
    * A failed execution (ie no fold-order returned)
    * @param startTimeInMilliseconds used to calculate how long the execution took
    */
  case class FailedExecution(implicit val startTimeInMilliseconds: Long)
      extends ExecutionResult(startTimeInMilliseconds)
}
