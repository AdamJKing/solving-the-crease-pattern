package uk.ac.aber.adk15.paper.newapi

import uk.ac.aber.adk15.geometry.{Line, Point, Polygon}
import uk.ac.aber.adk15.paper._
import uk.ac.aber.adk15.paper.newapi.NewPaperLayer._

import scala.Function.tupled

case class NewPaperLayer(private val shapes: Set[Polygon], private val folds: Map[Line, FoldType]) {

  def segmentOnLine(segmentLine: Line): (NewPaperLayer, NewPaperLayer) = {
    val (affectedShapes, unaffectedShapes) = shapes partition { shape =>
      (shape overlaps segmentLine.a) && (shape overlaps segmentLine.b)
    }

    val newShapes = affectedShapes flatMap (shape =>
      tupled(Set(_: Polygon, _: Polygon))(shape slice segmentLine))

    @inline
    def filterByPosition(positionFilter: Double => Boolean) =
      (newShapes ++ unaffectedShapes) filter (shape => positionFilter(shape compareTo segmentLine))

    val left  = filterByPosition(_ < 0)
    val right = filterByPosition(_ > 0)

    val leftFolds = folds filter tupled((line, _) => {
      line map { (a, b) =>
        left exists (shape => (shape contains a) && (shape contains b))
      }
    })

    val rightFolds = folds filter tupled((line, _) =>
      line map { (a, b) =>
        right exists { shape =>
          (shape contains a) && (shape contains b)
        }
    })

    val creasedLeftFolds  = leftFolds creaseFoldsAlong segmentLine
    val creasedRightFolds = rightFolds creaseFoldsAlong segmentLine

    (NewPaperLayer(left, creasedLeftFolds), NewPaperLayer(right, creasedRightFolds))
  }

  def rotateAround(rotationLine: Line): NewPaperLayer = {
    val rotatedShapes = shapes map (_ flatMap (_ reflectedOver rotationLine))
    val rotatedFolds = folds map tupled((line, foldType) =>
      (line mapValues (_ reflectedOver rotationLine)) -> (foldType match {
        case MountainFold    => ValleyFold
        case ValleyFold      => MountainFold
        case other: FoldType => other
      }))

    new NewPaperLayer(rotatedShapes, rotatedFolds)
  }

  def mergeWith(otherLayer: NewPaperLayer): Option[NewPaperLayer] = {
    val canBeAddedSafely = !(shapes exists (shape => otherLayer.shapes exists (_ overlaps shape)))
    val allOnEdge = otherLayer.shapes forall (shape =>
      shapes exists (s => s.points forall shape.isOnEdge))

    if (canBeAddedSafely && !allOnEdge) {
      Some(NewPaperLayer(shapes ++ otherLayer.shapes, folds ++ otherLayer.folds))
    } else None
  }

  def coversLine(line: Line): Boolean =
    shapes exists (shape => line map ((a, b) => coversPoint(a) && coversPoint(b)))

  def coversPoint(point: Point): Boolean =
    shapes exists (shape => (shape overlaps point) || (shape isOnEdge point))

  def surfaceArea: Double = (shapes map (_.surfaceArea)).sum

  def contains(foldLine: Line): Boolean = folds contains foldLine
  def contains(fold: NewFold): Boolean =
    contains(fold.line) && (folds get fold.line contains fold.foldType)

  def mountainFolds: Set[NewFold]   = extractFolds(MountainFold)
  def valleyFolds: Set[NewFold]     = extractFolds(ValleyFold)
  def creasedFolds: Set[NewFold]    = extractFolds(CreasedFold)
  def paperBoundaries: Set[NewFold] = extractFolds(PaperBoundary)

  def foldable: Set[Line]   = (mountainFolds ++ valleyFolds) map (_.line)
  def unfoldable: Set[Line] = (creasedFolds ++ paperBoundaries) map (_.line)

  override def equals(other: Any): Boolean = other match {
    case NewPaperLayer(otherShapes, otherFolds) => shapes == otherShapes && folds == otherFolds
    case _                                      => false
  }
  override def hashCode(): Int = 17 * (31 + shapes.hashCode()) * (31 + folds.hashCode())

  override def toString: String = {
    folds map tupled((line, foldType) => line map ((a, b) => s"$a $foldType $b")) mkString "\n"
  }

  private def extractFolds(foldType: FoldType) =
    (folds withFilter (_._2 == foldType) map tupled(NewFold)).toSet
}

object NewPaperLayer {
  def apply(folds: NewFold*) = new NewPaperLayer(
    Set(new Polygon(folds.toSet flatMap { (fold: NewFold) =>
      val points = fold.line.points
      Set(points._1, points._2)

    })),
    (for (fold <- folds) yield fold.line -> fold.foldType).toMap
  )

  implicit final class FoldMapOps(private val self: Map[Line, FoldType]) extends AnyVal {
    def creaseFoldsAlong(foldLine: Line): Map[Line, FoldType] =
      self map tupled((line, foldType) =>
        if (line alignsWith foldLine) (line, CreasedFold) else (line, foldType))
  }
}
