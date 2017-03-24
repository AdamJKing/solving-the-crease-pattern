package uk.ac.aber.adk15.paper

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.paper.Point.Helpers._

class CreasePattern(val layers: List[Set[Fold]]) extends Foldable {

  private val logger = Logger[CreasePattern]

  private val edges: List[Fold] = (layers reduce (_ ++ _)).toList

  override def creases: Set[Fold] =
    layers map (_ filter (fold => {
      fold.foldType == MountainFold || fold.foldType == ValleyFold
    })) reduce (_ ++ _)

  override def fold(edge: Fold): CreasePattern = {
    validateLegalEdge(edge)

    @inline def isOnLeft(e: Fold) =
      (e.toSet map (_ compareTo (edge.start, edge.end))).sum > 0

    @inline def isOnRight(e: Fold) =
      (e.toSet map (_ compareTo (edge.start, edge.end))).sum < 0

    @inline def isOnCentre(e: Fold) =
      (e.toSet map (_ compareTo (edge.start, edge.end))).sum == 0

    val foldedCreasePattern =
      (layers foldRight List[Set[Fold]]())((layer, acc) => {
        val creasedEdges = layer withFilter isOnCentre map (_.crease)

        val static = (layer filter isOnRight) ++ creasedEdges
        val folded = (layer withFilter isOnLeft map { rotateEdge(_, axis = edge) }) ++ creasedEdges

        (folded.headOption, static.headOption) match {
          case (Some(_), Some(_)) => folded +: acc :+ static
          case (Some(_), None)    => folded +: acc
          case (None, Some(_))    => acc :+ static
          case (None, None)       => acc
        }
      })

    logger debug s"$foldedCreasePattern"
    new CreasePattern(foldedCreasePattern)
  }

  override def equals(obj: Any): Boolean = obj match {
    case other: CreasePattern => other.layers == this.layers
    case _                    => false
  }

  override def toString = s"{ ${layers mkString ","} }"

  def size: Int = layers.length

  private def validateLegalEdge(edge: Fold) = {
    edges find (_ == edge) match {
      case Some(Fold(_, _, PaperBoundary)) => throw IllegalCreaseException(edge)
      case Some(Fold(_, _, CreasedFold))   => throw EdgeAlreadyCreasedException(edge)
      case _                               => true
    }
  }

  private def rotateEdge(edge: Fold, axis: Fold) = Fold(
    edge.start reflectedOver (axis.start, axis.end),
    edge.end reflectedOver (axis.start, axis.end),
    edge.foldType match {
      // when we fold a piece of paper, all unfolded creases receive the inverse assignment
      case MountainFold => ValleyFold
      case ValleyFold   => MountainFold

      // those already creased, or a physical boundary, will not change when folded
      case CreasedFold   => CreasedFold
      case PaperBoundary => PaperBoundary
    }
  )
}

object CreasePattern {
  def from(creaseLines: Fold*): CreasePattern = {
    new CreasePattern(List(Set[Fold](creaseLines: _*)))
  }

  def apply(layers: Set[Fold]*): CreasePattern = {
    new CreasePattern(layers.toList)
  }
}
