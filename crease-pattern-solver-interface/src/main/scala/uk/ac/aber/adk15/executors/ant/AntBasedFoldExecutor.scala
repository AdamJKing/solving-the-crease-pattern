package uk.ac.aber.adk15.executors.ant

import com.google.inject.Inject
import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.paper.{Fold, Foldable}
import uk.ac.aber.adk15.services.FoldSelectionService

import scala.concurrent.{ExecutionContext, Future}

trait AntBasedFoldExecutor {
  def findFoldOrder(creasePattern: Foldable, maxAnts: Int = 1)(
      implicit executionContext: ExecutionContext): Future[Option[List[Fold]]]
}

class AntBasedFoldExecutorImpl @Inject()(antTraverser: AntTraverser,
                                         foldSelectionService: FoldSelectionService)
    extends AntBasedFoldExecutor {

  private val logger = Logger[AntBasedFoldExecutorImpl]

  override def findFoldOrder(creasePattern: Foldable, maxAnts: Int = 1)(
      implicit executionContext: ExecutionContext): Future[Option[List[Fold]]] = {

    val operationTreeRoot = FoldNode(creasePattern, None)(implicitly(foldSelectionService))

    logger info s"Starting ant colony with $maxAnts ants."
    Future.firstCompletedOf {
      List.range(0, maxAnts) map (_ => Future(antTraverser traverseTree operationTreeRoot))
    }
  }
}
