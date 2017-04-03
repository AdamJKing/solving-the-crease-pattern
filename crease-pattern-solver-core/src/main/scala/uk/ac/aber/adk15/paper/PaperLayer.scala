package uk.ac.aber.adk15.paper

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.paper.Point.Helpers._

import scala.Function.tupled
import scala.annotation.tailrec
import scala.math.{max, min}

/**
  * A singular layer that may contain multiple Folds.
  *
  * @param folds the folds contained within this layer
  */
case class PaperLayer(private val folds: Seq[Fold]) {

  private val logger = Logger(s"PaperLayer${this.hashCode()}")

  /**
    * Separate the layer into two new layers based on a given line.
    *
    * When we separate the layers we also crease the line on which the
    * layer was cut. You can think of this as "ripping" the layer.
    *
    * @param start the first point on the line
    * @param end the second point on the line
    * @return the two new layers, as though the original were cut in two
    */
  def segmentOnLine(start: Point, end: Point): (PaperLayer, PaperLayer) = {
    @inline def isOnLeft(e: Fold)   = (e.toSet map (_ compareTo (start, end))).sum < 0
    @inline def isOnRight(e: Fold)  = (e.toSet map (_ compareTo (start, end))).sum > 0
    @inline def isOnCentre(e: Fold) = (e.toSet map (_ compareTo (start, end))).sum == 0

    val centreLines = (folds filter isOnCentre) map (_.crease)
    val leftLines   = (folds filter isOnLeft) ++ centreLines
    val rightLines  = (folds filter isOnRight) ++ centreLines

    (PaperLayer(leftLines), PaperLayer(rightLines))
  }

  /**
    * Rotates the layer around an axis given by two arbitrary points.
    *
    * Used mainly to simulate a folding action.
    *
    * @param axisStart the first point on the axis
    * @param axisEnd the last point on the axis
    * @return the rotated layer
    */
  def rotateAround(axisStart: Point, axisEnd: Point): PaperLayer = {
    PaperLayer(folds map {
      case Fold(start, end, foldType) =>
        Fold(
          start reflectedOver (axisStart, axisEnd),
          end reflectedOver (axisStart, axisEnd),
          foldType match {
            case MountainFold  => ValleyFold
            case ValleyFold    => MountainFold
            case CreasedFold   => CreasedFold
            case PaperBoundary => PaperBoundary
          }
        )
    })
  }

  /**
    * Merges two layers together to form one layer. It will fail
    * if the layers cannot be merged without overlapping. This is
    * because overlapping paper must be on another layer.
    *
    * TODO: Update winding number reference
    *
    * @param otherLayer the layer to merge with
    * @return an optional merged paper layer, with none if the merge failed
    */
  def mergeWith(otherLayer: PaperLayer): Option[PaperLayer] = {
    @tailrec
    def findWindingNumber(p: Point, polygon: Seq[Point], startWn: Int = 0): Int = {
      polygon.toList match {
        case a :: b :: rest =>
          if (a.y <= p.y && b.y > p.y && (p compareTo (a, b)) > 0)
            findWindingNumber(p, b :: rest, startWn + 1)
          else if (b.y <= p.y && (p compareTo (a, b)) < 0)
            findWindingNumber(p, b :: rest, startWn - 1)
          else
            findWindingNumber(p, b :: rest, startWn)

        case _ => startWn
      }
    }

    val currentLayerPoints = for (fold <- folds) yield (fold.start, fold.end)
    val otherLayerPoints   = for (fold <- otherLayer.folds) yield (fold.start, fold.end)

    def isInBoundingBox(p: Point, points: Seq[(Point, Point)]) = {
      val sortedValuesX = (points flatMap tupled((a, b) => Seq(a.x, b.x))).sorted
      val sortedValuesY = (points flatMap tupled((a, b) => Seq(a.y, b.y))).sorted

      val (xMax, xMin) = (sortedValuesX.last, sortedValuesX.head)
      val (yMax, yMin) = (sortedValuesY.last, sortedValuesY.head)

      (p.x <= xMax && p.x >= xMin) && (p.y <= yMax && p.y >= yMin)
    }

    def isOnEdge(p: Point, polygon: Seq[(Point, Point)]) = {
      polygon exists tupled((a: Point, b: Point) => {
        val isInRangeX        = (p.x >= min(a.x, b.x)) && (p.x <= max(a.x, b.x))
        val isInRangeY        = (p.y >= min(a.y, b.y)) && (p.y <= max(a.y, b.y))
        val equalsEitherPoint = p == a || p == b
        val isOnSameLine      = math.abs(p gradientTo a) == math.abs(p gradientTo b)

        (isInRangeX && isInRangeY) && {
          if (equalsEitherPoint) true
          else isOnSameLine
        }
      })
    }

    def isValidPoint(p: Point, polygon: Seq[(Point, Point)]): Boolean = {
      val points = polygon flatMap tupled(Seq(_, _))
      if (isInBoundingBox(p, polygon))
        isOnEdge(p, polygon) || findWindingNumber(p, points :+ points.head) == 0
      else true
    }

    val canBeAddedSafely = (otherLayer.folds forall (fold => {
      isValidPoint(fold.start, currentLayerPoints) && isValidPoint(fold.end, currentLayerPoints)
    })) && (folds forall (fold => {
      isValidPoint(fold.start, otherLayerPoints) && isValidPoint(fold.end, otherLayerPoints)
    }))

    val allOnEdge = otherLayer.folds forall (fold =>
      isOnEdge(fold.start, currentLayerPoints) || isOnEdge(fold.end, currentLayerPoints))

    if (canBeAddedSafely && !allOnEdge)
      Some(PaperLayer(folds ++ otherLayer.folds))
    else None
  }

  /**
    * Returns the total surface area covered by this layer.
    *
    * @return the calculated surface area
    */
  def surfaceArea(): Double = {
    val points = (creasedFolds() ++ paperBoundaries()) flatMap (_.toSet)

    (points.distinct sliding 3 foldLeft 0.0) { (totalArea, group) =>
      group match {
        case Seq(a: Point, b: Point, c: Point) =>
          // http://www.mathopenref.com/coordtrianglearea.html
          math.abs((a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2) + totalArea

        case _ => totalArea
      }
    }
  }

  def mountainFolds(): Seq[Fold]   = folds filter (_.foldType == MountainFold)
  def valleyFolds(): Seq[Fold]     = folds filter (_.foldType == ValleyFold)
  def creasedFolds(): Seq[Fold]    = folds filter (_.foldType == CreasedFold)
  def paperBoundaries(): Seq[Fold] = folds filter (_.foldType == PaperBoundary)

  def contains(fold: Fold): Boolean            = folds contains fold
  def exists(test: (Fold) => Boolean): Boolean = folds exists test

  override def equals(that: Any): Boolean = that match {
    case PaperLayer(otherFolds) => folds.toSet == otherFolds.toSet
    case _                      => false
  }

  override def hashCode(): Int = folds.toSet.hashCode()

  override def toString: String = s"${folds.mkString("\n\t")}"
}
