package uk.ac.aber.adk15.paper

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.paper.Point.Helpers._

import scala.math.{abs, max}

class CreasePattern(val layers: List[Set[Fold]]) extends Foldable {

  private val logger = Logger[CreasePattern]

  private val edges: List[Fold] = (layers reduce (_ ++ _)).toList

  override def creases: Set[Fold] =
    layers map (_ filter (fold => {
      fold.foldType == MountainFold || fold.foldType == ValleyFold
    })) reduce (_ ++ _)

  override def fold(edge: Fold): CreasePattern = {
    validateLegalEdge(edge)

    val (layersToFold, layersToLeave) = layers partition (_ contains edge)
    val (left, right)                 = sliceLayerStack(layersToFold, edge)

    val newLayers =
      if (surfaceArea(left) < surfaceArea(right))
        left.map(_ map (fold => rotateEdge(fold, edge))) ++ right
      else
        right.map(_ map (fold => rotateEdge(fold, edge))) ++ left

    new CreasePattern(repair(newLayers, layersToLeave, edge.foldType))
  }

  override def equals(obj: Any): Boolean = obj match {
    case other: CreasePattern => other.layers == this.layers
    case _                    => false
  }

  override def hashCode(): Int = {
    17 * 31 * layers.hashCode()
  }

  override def toString = s"{ ${layers mkString ","} }"

  def size: Int = layers.length

  private def validateLegalEdge(edge: Fold) = {
    edges find (_ == edge) match {
      case Some(Fold(_, _, PaperBoundary)) => throw new IllegalFoldException(edge)
      case Some(Fold(_, _, CreasedFold))   => throw new EdgeAlreadyCreasedException(edge)
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

  private def sliceLayerStack(stack: List[Set[Fold]], slice: Fold) = {
    @inline def isOnLeft(e: Fold) =
      (e.toSet map (_ compareTo (slice.start, slice.end))).sum > 0

    @inline def isOnRight(e: Fold) =
      (e.toSet map (_ compareTo (slice.start, slice.end))).sum < 0

    @inline def isOnCentre(e: Fold) =
      (e.toSet map (_ compareTo (slice.start, slice.end))).sum == 0

    def creaseAndKeepIf(layer: Set[Fold], condition: Fold => Boolean) = {
      layer filter (f => condition(f) || isOnCentre(f)) map (x =>
        if (isOnCentre(x)) x.crease
        else x)
    }

    (stack map (layer => creaseAndKeepIf(layer, isOnLeft)),
     stack map (layer => creaseAndKeepIf(layer, isOnRight)))
  }

  private def surfaceArea(layers: List[Set[Fold]]): Double = {
    def calculateArea(layer: Set[Fold]) = {
      val points = (layer flatMap (_.toSet)).toSeq

      (points grouped 3).map { group =>
        if (group.size < 3) 0
        else {
          val (a: Point, b: Point, c: Point) = (group.head, group(1), group(2))
          // http://www.mathopenref.com/coordtrianglearea.html
          abs((a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2)
        }
      }.sum
    }

    (layers foldLeft 0.0)((maxArea, layer) => max(calculateArea(layer), maxArea))
  }

  private def repair(toRepair: List[Set[Fold]],
                     repairWith: List[Set[Fold]],
                     foldType: FoldType): List[Set[Fold]] = {

    if (repairWith.isEmpty) return toRepair

    def containsNoOverlaps(a: Set[Fold], b: Set[Fold]): Boolean = {
      val (left, right) = (a flatMap (_.toSet), b flatMap (_.toSet))

      left forall (testPoint =>
        ((right sliding 2) foldLeft false)((flop, points) => {
          if ((points.head.y > testPoint.y) != (points.last.y > testPoint.y) && (testPoint.x < (points.last.x - points.head.x * ((testPoint.y - points.head.y / points.last.y - points.head.y) + points.head.x))))
            !flop
          else
            flop
        }))
    }

    foldType match {
      case MountainFold =>
        val tails = toRepair.tails
        val inits = repairWith.inits
        val potentialOverlaps = (tails dropWhile (_ => tails.size > inits.size))
          .zip(inits dropWhile (_ => inits.size > tails.size))

        val potentialOverlap = potentialOverlaps find (overlap =>
          (for (i <- overlap._1; j <- overlap._2) yield (i, j)) forall (layers =>
            containsNoOverlaps(layers._1, layers._2)))

        potentialOverlap.map(overlap => {
          val notInOverlap = (repairWith filter potentialOverlap.contains,
                              toRepair filter potentialOverlap.contains)

          notInOverlap._1 ++ (for (i <- overlap._1; j <- overlap._2)
            yield i ++ j) ++ notInOverlap._2
        }) getOrElse repairWith ++ toRepair

      case ValleyFold =>
        val potentialOverlaps = toRepair.inits zip repairWith.tails

        val potentialOverlap = potentialOverlaps find (overlap =>
          (for (i <- overlap._1; j <- overlap._2) yield (i, j)) forall (layers =>
            containsNoOverlaps(layers._1, layers._2)))

        potentialOverlap.map(overlap => {
          val notInOverlap = (toRepair filter potentialOverlap.contains,
                              repairWith filter potentialOverlap.contains)

          notInOverlap._1 ++ (for (i <- overlap._1; j <- overlap._2)
            yield i ++ j) ++ notInOverlap._2
        }) getOrElse toRepair ++ repairWith

      case _ => throw new IllegalStateException("Cannot repair directionless fold")
    }
  }
}

object CreasePattern {
  def from(creaseLines: Fold*): CreasePattern = {
    new CreasePattern(List(Set[Fold](creaseLines: _*)))
  }

  def apply(layers: Set[Fold]*): CreasePattern = {
    new CreasePattern(layers.toList)
  }
}
