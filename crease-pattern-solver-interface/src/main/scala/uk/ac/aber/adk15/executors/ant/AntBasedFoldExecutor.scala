package uk.ac.aber.adk15.executors.ant

import com.google.inject.Inject
import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.paper.{CreasePattern, Fold}
import uk.ac.aber.adk15.services.FoldSelectionService

import scala.concurrent.{ExecutionContext, Future}

trait AntBasedFoldExecutor {
  def findFoldOrder(creasePattern: CreasePattern, maxAnts: Int)(
      implicit executionContext: ExecutionContext): Future[Option[List[Fold]]]
}

class AntBasedFoldExecutorImpl @Inject()(antTraverser: AntTraverser,
                                         foldSelectionService: FoldSelectionService)
    extends AntBasedFoldExecutor {

  private val logger = Logger[AntBasedFoldExecutorImpl]

  override def findFoldOrder(creasePattern: CreasePattern, maxAnts: Int)(
      implicit executionContext: ExecutionContext): FoldOrder = {

    val operationTreeRoot = FoldNode(creasePattern, None)(implicitly(foldSelectionService))

    logger info s"Starting ant colony with $maxAnts ants."
    Future.firstCompletedOf {
      List.range(0, maxAnts) map (_ => Future(antTraverser traverseTree operationTreeRoot))

    } map (FoldOrder(_)) getOrElse Future.successful(None)
  }

  case class FoldOrder(foldOrder: List[Fold])
}
