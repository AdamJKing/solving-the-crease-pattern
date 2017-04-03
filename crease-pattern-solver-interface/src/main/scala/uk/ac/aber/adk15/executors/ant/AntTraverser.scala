package uk.ac.aber.adk15.executors.ant

import java.util.concurrent.atomic.AtomicBoolean

import com.google.inject.Inject
import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.paper.Fold

import scala.annotation.tailrec
import scala.collection.concurrent.TrieMap
import scala.collection.mutable

trait AntTraverser {
  def traverseTree(root: FoldNode): Option[List[Fold]]

  protected def traverse(currentNode: FoldNode, visitedNodes: List[FoldNode]): Option[List[Fold]]
  protected def updateWeights(nodes: List[FoldNode]): Int
  protected def selectChild(children: Set[FoldNode]): FoldNode
}

class AntTraverserImpl @Inject()(diceRollService: DiceRollService) extends AntTraverser {

  private val logger = Logger[AntTraverser]

  private val nodeWeights: mutable.Map[FoldNode, Int] = TrieMap[FoldNode, Int]() withDefaultValue 0
  private val solutionFound: AtomicBoolean            = new AtomicBoolean(false)

  override def traverseTree(root: FoldNode): Option[List[Fold]] = {
    val foldOrder = {
      (Stream continually traverse(root, List(root))
        dropWhile (_.isEmpty && !solutionFound.get)).head
    }

    logger info "I found the goal!"
    solutionFound compareAndSet (false, true)

    foldOrder
  }

  @tailrec
  protected final def traverse(currentNode: FoldNode,
                               visitedNodes: List[FoldNode]): Option[List[Fold]] = {

    val isEndState     = currentNode.children.isEmpty
    val isSuccessState = currentNode.model.folds.isEmpty

    if (isEndState) {
      if (isSuccessState) {
        Some(visitedNodes withFilter (_.fold.isDefined) map (_.fold.get))
      } else {
        nodeWeights(currentNode) = 0
        updateWeights(visitedNodes)
        None
      }
    } else {
      traverse(selectChild(currentNode.children), currentNode :: visitedNodes)
    }
  }

  protected def updateWeights(nodes: List[FoldNode]): Int = nodes match {
    case x :: xs =>
      nodeWeights(x) += updateWeights(xs)
      nodeWeights(x)

    case List(x) =>
      nodeWeights(x) += 1
      nodeWeights(x)
  }

  protected def selectChild(children: Set[FoldNode]): FoldNode = {
    val weights = children.toList map (node =>
      (nodeWeights(node) * diceRollService.randomDiceRoll(floor = 0.6, ceil = 1.0)).toInt)

    children.toList(diceRollService.randomWeightedDiceRoll(weights))
  }
}
