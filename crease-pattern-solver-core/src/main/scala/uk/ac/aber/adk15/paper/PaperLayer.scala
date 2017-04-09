package uk.ac.aber.adk15.paper

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.paper.Point.Helpers._

import scala.Function.tupled
import scala.annotation.tailrec
import scala.math.{max, min, signum}

/**
  * A singular layer that may contain multiple Folds.
  *
  * @param folds the folds contained within this layer
  */
case class PaperLayer(folds: List[Fold]) {

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
    val canBeAddedSafely =
      !(otherLayer.folds.exists(coversFold(_, failsOnEdge = false))
        && folds.exists(otherLayer coversFold (_, failsOnEdge = false)))

    val currentLayerPoints = for (fold <- folds) yield (fold.start, fold.end)

    val allOnEdge = otherLayer.folds forall (fold =>
      isOnEdge(fold.start, currentLayerPoints) || isOnEdge(fold.end, currentLayerPoints))

    if (canBeAddedSafely && !allOnEdge) {
      logger debug s"Layers can be combined (layer=$this combined with otherLayer=$otherLayer)"
      Some(PaperLayer(folds ++ otherLayer.folds))
    } else None
  }

  /**
    * Returns the total surface area covered by this layer.
    *
    * @return the calculated surface area
    */
  def surfaceArea: Double = {
    val points = (creasedFolds ++ paperBoundaries) flatMap (_.toSet)

    (points.distinct sliding 3 foldLeft 0.0) { (totalArea, group) =>
      group match {
        case List(a: Point, b: Point, c: Point) =>
          // http://www.mathopenref.com/coordtrianglearea.html
          math.abs((a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2) + totalArea

        case _ => totalArea
      }
    }
  }

  def coversFold(foldToTest: Fold, failsOnEdge: Boolean = true): Boolean = {
    @tailrec
    def findWindingNumber(p: Point, polygon: List[Point], startWn: Int = 0): Int = {
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

    def isInBoundingBox(p: Point, points: List[(Point, Point)]) = {
      val sortedValuesX = (points flatMap tupled((a, b) => List(a.x, b.x))).sorted
      val sortedValuesY = (points flatMap tupled((a, b) => List(a.y, b.y))).sorted

      val (xMax, xMin) = (sortedValuesX.last, sortedValuesX.head)
      val (yMax, yMin) = (sortedValuesY.last, sortedValuesY.head)

      (p.x <= xMax && p.x >= xMin) && (p.y <= yMax && p.y >= yMin)
    }

    val layerPoints = for (fold <- folds) yield (fold.start, fold.end)

    val edgeCheckedFold = accountForEdges(foldToTest, layerPoints)

    edgeCheckedFold.toSet exists (pointOnFold => {
      if (isInBoundingBox(pointOnFold, layerPoints)) {
        val polygon = layerPoints flatMap tupled(List(_, _))
        findWindingNumber(pointOnFold, polygon :+ polygon.head) != 0
      } else false
    })
  }

  def mountainFolds: List[Fold]     = folds filter (_.foldType == MountainFold)
  def valleyFolds: List[Fold]       = folds filter (_.foldType == ValleyFold)
  def creasedFolds: List[Fold]      = folds filter (_.foldType == CreasedFold)
  def paperBoundaries: List[Fold] = folds filter (_.foldType == PaperBoundary)

  def contains(fold: Fold): Boolean            = folds contains fold
  def exists(test: (Fold) => Boolean): Boolean = folds exists test

  override def equals(that: Any): Boolean = that match {
    case PaperLayer(otherFolds) => folds.toSet == otherFolds.toSet
    case _                      => false
  }

  override def hashCode(): Int = folds.toSet.hashCode()

  override def toString: String = s"${folds.mkString("\n\t")}"

  private def isOnEdge(p: Point, polygon: List[(Point, Point)]) = {
    polygon exists tupled((a: Point, b: Point) => {
      val isInRangeX        = (p.x >= min(a.x, b.x)) && (p.x <= max(a.x, b.x))
      val isInRangeY        = (p.y >= min(a.y, b.y)) && (p.y <= max(a.y, b.y))
      val equalsEitherPoint = p == a || p == b
      val isOnSameLine      = (p gradientTo a) == (p gradientTo b)

      (isInRangeX && isInRangeY) && {
        if (equalsEitherPoint) true
        else isOnSameLine
      }
    })
  }

  private def accountForEdges(foldToTest: Fold, layerPoints: List[(Point, Point)]): Fold = {
    val distinctPoints = layerPoints flatMap tupled(Set(_, _))
    def average(value: Point => Double) =
      (0.0 /: distinctPoints)(_ + value(_)) / distinctPoints.length

    val centreOfMass = Point(average(_.x), average(_.y))

    def movePointTowardsCentreOfMass(point: Point) = {
      val diff = centreOfMass - point
      Point(point.x - signum(diff.x), point.y - signum(diff.y))
    }

    foldToTest mapPoints { point =>
      if (isOnEdge(point, layerPoints))
        movePointTowardsCentreOfMass(point)
      else
        point
    }
  }
}

object PaperLayer {
  def from(folds: Fold*) = PaperLayer(folds.toList)
}
