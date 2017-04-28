package uk.ac.aber.adk15.paper.newapi

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.geometry.{Line, Polygon}
import uk.ac.aber.adk15.paper._
import uk.ac.aber.adk15.paper.newapi.NewPaperLayer._

import scala.Function.tupled

case class NewPaperLayer(private val shapes: Set[Polygon], private val folds: Map[Line, FoldType]) {

  private val logger = Logger[NewPaperLayer]

  def segmentOnLine(segmentLine: Line): (NewPaperLayer, NewPaperLayer) = {
    val (affectedShapes, unaffectedShapes) = shapes partition { shape =>
      (shape overlaps segmentLine.a) && (shape overlaps segmentLine.b)
    }

    val newShapes = affectedShapes flatMap (shape =>
      tupled(Set(_: Polygon, _: Polygon))(shape slice segmentLine))

    def filterByPosition(positionFilter: Double => Boolean) =
      (newShapes ++ unaffectedShapes) filter (shape => positionFilter(shape compareTo segmentLine))

    val left  = filterByPosition(_ < 0)
    val right = filterByPosition(_ > 0)

    val (leftFolds, rightFolds) = folds partition {
      tupled((line, _) =>
        line map { (a, b) =>
          left exists { shape =>
            (shape contains a) && (shape contains b)
          }
      })
    }

    val creasedLeftFolds  = leftFolds creaseFoldsAlong segmentLine
    val creasedRightFolds = rightFolds creaseFoldsAlong segmentLine

    (NewPaperLayer(left, creasedLeftFolds), NewPaperLayer(right, creasedRightFolds))
  }

  def rotateAround(rotationLine: Line): NewPaperLayer = {
    val rotatedShapes = shapes map (_ flatMap (_ reflectedOver rotationLine))
    val rotatedFolds = folds map tupled((line, foldType) =>
      (line flatMap (_ reflectedOver rotationLine)) -> (foldType match {
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

  def coversLine(line: Line): Boolean = shapes exists { shape =>
    line map ((a, b) => (shape overlaps a) && (shape overlaps b))
  }

  def surfaceArea: Double = (shapes map (_.surfaceArea)).sum

  def contains(fold: NewFold): Boolean =
    shapes exists { shape =>
      val containsLine      = fold.line map ((a, b) => (shape contains a) && (shape contains b))
      val hasFoldAssignment = folds(fold.line) == fold.foldType

      containsLine && hasFoldAssignment
    }

  def mountainFolds: Set[Line]   = extractFolds(MountainFold)
  def valleyFolds: Set[Line]     = extractFolds(ValleyFold)
  def creasedFolds: Set[Line]    = extractFolds(CreasedFold)
  def paperBoundaries: Set[Line] = extractFolds(PaperBoundary)

  def foldable: Set[Line]   = mountainFolds ++ valleyFolds
  def unfoldable: Set[Line] = creasedFolds ++ paperBoundaries

  override def equals(other: Any): Boolean = other match {
    case NewPaperLayer(otherShapes, otherFolds) => shapes == otherShapes && folds == otherFolds
    case _                                      => false
  }
  override def hashCode(): Int = 17 * (31 + shapes.hashCode()) * (31 + folds.hashCode())

  override def toString: String = {
    folds map tupled((line, foldType) => line map ((a, b) => s"$a $foldType $b")) mkString "\n"
  }

  private def extractFolds(foldType: FoldType) =
    (folds filter { case (_, ft) => foldType == ft }).keySet
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
      self map tupled { (line, foldType) =>
        if (line alignsWith foldLine) (line, CreasedFold)
        else (line, foldType)
      }
  }
}
