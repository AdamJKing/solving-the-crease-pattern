package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.geometry.{Line, Point, Polygon, Rectangle}
import uk.ac.aber.adk15.paper.PaperLayer._
import uk.ac.aber.adk15.paper.fold._

import scala.Function.tupled

case class PaperLayer(private val shapes: Set[Polygon], private val folds: Map[Line, FoldType]) {
  require(shapes.nonEmpty, "Paper layer must have some content.")

  def segmentOnLine(segmentLine: Line): (Option[PaperLayer], Option[PaperLayer]) = {
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

    val leftLayer  = if (left.nonEmpty) Some(PaperLayer(left, creasedLeftFolds)) else None
    val rightLayer = if (right.nonEmpty) Some(PaperLayer(right, creasedRightFolds)) else None

    (leftLayer, rightLayer)
  }

  def rotateAround(rotationLine: Line): PaperLayer = {
    val rotatedShapes = shapes map (_ flatMap (_ reflectedOver rotationLine))
    val rotatedFolds = folds map tupled((line, foldType) =>
      (line mapValues (_ reflectedOver rotationLine)) -> (foldType match {
        case MountainFold    => ValleyFold
        case ValleyFold      => MountainFold
        case other: FoldType => other
      }))

    new PaperLayer(rotatedShapes, rotatedFolds)
  }

  def mergeWith(otherLayer: PaperLayer): Option[PaperLayer] = {
    val canBeAddedSafely = !(shapes exists (shape => otherLayer.shapes exists (_ overlaps shape)))
    val allOnEdge = otherLayer.shapes forall (shape =>
      shapes exists (s => s.points forall shape.isOnEdge))

    if (canBeAddedSafely && !allOnEdge) {
      Some(PaperLayer(shapes ++ otherLayer.shapes, folds ++ otherLayer.folds))
    } else None
  }

  def coversLine(line: Line): Boolean =
    shapes exists (shape => line map ((a, b) => coversPoint(a) || coversPoint(b)))

  def coversPoint(point: Point): Boolean =
    shapes exists (shape => (shape overlaps point) && !(shape isOnEdge point))

  def isOnEdge(point: Point): Boolean = shapes exists (_ isOnEdge point)

  def surfaceArea: Double = (shapes map (_.surfaceArea)).sum

  def boundingBox: Rectangle = (shapes map (_.boundingBox)) reduceLeft (_ combineWith _)

  def contains(foldLine: Line): Boolean = folds contains foldLine
  def contains(fold: Fold): Boolean =
    contains(fold.line) && (folds get fold.line contains fold.foldType)

  def exists(predicate: (Fold) => Boolean): Boolean = (foldable ++ unfoldable) exists predicate

  def mountainFolds: Set[Fold]   = extractFolds(MountainFold)
  def valleyFolds: Set[Fold]     = extractFolds(ValleyFold)
  def creasedFolds: Set[Fold]    = extractFolds(CreasedFold)
  def paperBoundaries: Set[Fold] = extractFolds(PaperBoundary)

  def foldable: Set[Fold]   = mountainFolds ++ valleyFolds
  def unfoldable: Set[Fold] = creasedFolds ++ paperBoundaries

  override def equals(other: Any): Boolean = other match {
    case PaperLayer(otherShapes, otherFolds) => shapes == otherShapes && folds == otherFolds
    case _                                   => false
  }

  override def hashCode(): Int = 17 * (31 + shapes.hashCode()) * (31 + folds.hashCode())

  override def toString: String = {
    folds map tupled((line, foldType) => line map ((a, b) => s"$a $foldType $b")) mkString "\n"
  }

  private def extractFolds(foldType: FoldType) =
    (folds withFilter (_._2 == foldType) map tupled(Fold)).toSet
}

object PaperLayer {
  def apply(folds: Fold*) = new PaperLayer(
    Set(new Polygon(folds.toSet flatMap { (fold: Fold) =>
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
