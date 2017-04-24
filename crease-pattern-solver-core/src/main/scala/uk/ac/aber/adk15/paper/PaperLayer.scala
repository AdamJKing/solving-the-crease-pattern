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
case class PaperLayer(folds: List[Fold]) {

  private val logger = Logger(s"PaperLayer${this.hashCode()}")

  /**
    * Separate the layer into two new layers based on a given line.
    *
    * When we separate the layers we also crease the line on which the
    * layer was cut. You can think of this as "ripping" the layer.
    *
    * @param fold the edge to use as the basis for the cut
    * @return the two new layers, as though the original were cut in two
    */
  def segmentOnFold(fold: Fold): (PaperLayer, PaperLayer) = {
    val (start, end) = (fold.start, fold.end)

    def isOnLeft(e: Fold)   = (e.toSet map (_ compareTo (start, end))).sum < 0
    def isOnRight(e: Fold)  = (e.toSet map (_ compareTo (start, end))).sum > 0
    def isOnCentre(e: Fold) = (e.toSet map (_ compareTo (start, end))).sum == 0

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

  def coversFold(foldToTest: Fold, failsOnEdge: Boolean = true): Boolean = {
    // http://geomalgorithms.com/a03-_inclusion.html
    @tailrec
    def findWindingNumber(p: Point, polygon: List[Point], startWn: Int = 0): Int = {
      polygon match {
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

    val layerPoints = for (fold <- (creasedFolds ++ paperBoundaries).toList)
      yield (fold.start, fold.end)

    if (((creasedFolds ++ paperBoundaries) contains foldToTest) || (isOnEdge(
          foldToTest.start,
          layerPoints) ^ isOnEdge(foldToTest.end, layerPoints))) {
      val edgeCheckedFold = accountForEdges(foldToTest, layerPoints)

      edgeCheckedFold.toSet exists (pointOnFold => {
        if (isInBoundingBox(pointOnFold, layerPoints)) {
          val polygon = layerPoints map (_._1)
          findWindingNumber(pointOnFold, polygon :+ polygon.head) != 0
        } else false
      })
    } else {
      isOnEdge(foldToTest.start, layerPoints) && isOnEdge(foldToTest.end, layerPoints) && failsOnEdge
    }
  }

  def surfaceArea: Double = {
    val points = ((creasedFolds ++ paperBoundaries) map (_.start)).toList

    // define a way of ordering points that works for our calculation
    implicit val pointOrder = Ordering by ((p: Point) => p.x + p.y)

    // run a sliding window over the list of points
    // we take three points at a time, calculating the area of that triangle
    // and summing them to find the total area
    (0.0 /: (points.sorted sliding 3))((totalArea, triangle) =>
      totalArea + (triangle match {
        case x :: y :: z :: _ => calculateAreaOfTriangle(x, y, z)
        case _                => 0.0
      }))
  }

  def mountainFolds: Set[Fold]   = (folds filter (_.foldType == MountainFold)).toSet
  def valleyFolds: Set[Fold]     = (folds filter (_.foldType == ValleyFold)).toSet
  def creasedFolds: Set[Fold]    = (folds filter (_.foldType == CreasedFold)).toSet
  def paperBoundaries: Set[Fold] = (folds filter (_.foldType == PaperBoundary)).toSet

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
      val isOnSameLine = {
        (p.x == a.x && p.x == b.x && a.x == b.x) ||
        (p.y == a.y && p.y == b.y && a.y == b.y) ||
        (p gradientTo a) == (p gradientTo b)
      }

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

    def movePointAwayFromCentreOfMass(point: Point) = {
      val alteredX = point.x - math.signum(centreOfMass.x - point.x)
      val alteredY = point.y - math.signum(centreOfMass.y - point.y)

      Point(alteredX, alteredY)
    }

    foldToTest mapPoints { point =>
      if (isOnEdge(point, layerPoints))
        movePointAwayFromCentreOfMass(point)
      else
        point
    }
  }

  private def calculateAreaOfTriangle(x: Point, y: Point, z: Point) = {
    // http://www.mathopenref.com/coordtrianglearea.html
    def f(a: Point, b: Point, c: Point) = a.x * (b.y - c.y)
    math.abs(f(x, y, z) + f(y, z, x) + f(z, x, y)) / 2
  }

}

object PaperLayer {
  def from(fold: Fold, folds: Fold*) = PaperLayer(fold +: folds.toList)
}
