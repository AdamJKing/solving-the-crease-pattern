package uk.ac.aber.adk15.paper

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.Point
import uk.ac.aber.adk15.PointHelpers._
import uk.ac.aber.adk15.paper.CreasePatternPredef.Layer

class CreasePattern(private val layers: Seq[Layer]) extends Foldable {

  private val logger = Logger[CreasePattern]

  lazy val edges: Traversable[PaperEdge[Point]] = layers reduce (_ ++ _)

  override def fold(edge: PaperEdge[Point]): CreasePattern = {
    validateLegalEdge(edge)

    @inline def isOnLeft(e: PaperEdge[Point]) =
      (e map (_ compareTo (edge.start, edge.end))).sum > 0

    @inline def isOnRight(e: PaperEdge[Point]) =
      (e map (_ compareTo (edge.start, edge.end))).sum < 0

    val folded = layers.foldRight(List[Layer]())((layer, acc) => {
      val static = (layer filter isOnRight) + edge.crease
      val folded = (layer withFilter isOnLeft map { rotateEdge(_, axis = edge) }) + edge.crease

      logger debug s"$folded"
      Layer(static) +: acc :+ Layer(folded)
    })

    new CreasePattern(folded)
  }

  override def equals(obj: Any): Boolean = obj match {
    case other: CreasePattern => other.layers == this.layers
    case _                    => false
  }

  override def toString: String = s"{ ${layers mkString ","} }"

  private def validateLegalEdge(edge: PaperEdge[Point]) = {
    edges find (_ == edge) match {
      case Some(PaperEdge(_, _, PaperBoundary)) => throw IllegalCreaseException(edge)
      case Some(PaperEdge(_, _, CreasedFold))   => throw EdgeAlreadyCreasedException(edge)
      case None                                 => throw IllegalCreaseException(edge)
      case _                                    => true
    }
  }

  private def rotateEdge(edge: PaperEdge[Point], axis: PaperEdge[Point]) = PaperEdge(
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
  def from(creaseLines: PaperEdge[Point]*): CreasePattern = {
    new CreasePattern(List(Layer(creaseLines: _*)))
  }

  def apply(layers: Layer*): CreasePattern = {
    new CreasePattern(layers)
  }
}
