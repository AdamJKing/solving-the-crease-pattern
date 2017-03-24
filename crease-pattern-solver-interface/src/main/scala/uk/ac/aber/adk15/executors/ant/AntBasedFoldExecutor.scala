package uk.ac.aber.adk15.executors.ant

import java.util.concurrent.atomic.AtomicBoolean

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.executors.FoldExecutor
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.paper.CreasePatternPredef.Helpers._
import uk.ac.aber.adk15.paper.{Fold, Foldable}
import uk.ac.aber.adk15.services.FoldSelectionService

import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class AntBasedFoldExecutor(foldSelectionService: FoldSelectionService, config: Config)
    extends FoldExecutor {

  private val logger = Logger[AntBasedFoldExecutor]

  private val weightsMap = TrieMap[OperationNode, Int]() withDefault (_ => 0)

  private val solutionFound: AtomicBoolean = new AtomicBoolean(false)

  override def findFoldOrder(creasePattern: Foldable)(
      implicit executionContext: ExecutionContext): Future[Option[List[Fold]]] = {

    val operationTree = OperationNode(creasePattern, None)

    logger info "Starting ant colony"
    Future.firstCompletedOf {
      List.range(0, config.maxThreads).map(_ => executeTraversal(operationTree))
    }
  }

  private def executeTraversal(root: OperationNode)(
      implicit executionContext: ExecutionContext): Future[Option[List[Fold]]] = {

    def traverse(root: OperationNode): Either[List[OperationNode], List[Fold]] = {
      if (root.children.isEmpty) {
        if (root.model.creases.isEmpty) Right(root.fold map (List(_)) getOrElse List())
        else Left(List(root))

      } else {
        traverse(root.selectChild) match {
          case Right(folds)     => Right(root.fold map (_ :: folds) getOrElse folds)
          case Left(operations) => Left(root :: operations)
        }
      }
    }

    Future {
      if (solutionFound.get()) None

      var result = traverse(root)

      while (result.isLeft) {
        logger info "I didn't find the goal; updating weights..."
        result.left.get map (weightsMap(_) += 1)
        result = traverse(root)
      }

      logger info "I found the goal!"
      solutionFound set true
      Some(result.right.get)
    }
  }

  case class OperationNode(model: Foldable, fold: Option[Fold]) {
    lazy val children: Set[OperationNode] =
      (foldSelectionService getAvailableOperations model) map (f =>
        OperationNode(model <~~ f, Some(f)))

    def selectChild: OperationNode = {
      @inline def calculateWeight(weight: Double) =
        1.0 - (weight / totalWeight) * Random.nextDouble()

      lazy val weights     = children map (weightsMap(_))
      lazy val totalWeight = weights.sum

      lazy val probabilities =
        (for { c <- children; w <- weights } yield c -> calculateWeight(w)) toMap

      var cumulativeProp = 0.0
      val diceRoll       = Random.nextDouble()

      children find (c => {
        cumulativeProp += probabilities(c)
        diceRoll <= cumulativeProp

      }) getOrElse {
        // worst case we default to randomly selecting a child
        logger debug "A child was randomly selected."
        children.toList(Random.nextInt(children.size))
      }
    }
  }
}
