package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.geometry.{Line, Point, Polygon, Rectangle}
import uk.ac.aber.adk15.paper.PaperLayer._
import uk.ac.aber.adk15.paper.fold._

import scala.Function.tupled

/**
  * Represents a single layer in a [[CreasePattern]].
  * It can be considered similar to a crease-pattern that also includes
  * [[CreasedFold]]s.
  *
  * A layer contains one or more [[Polygon]]s , which represent distinct
  * pieces of paper contained in the layer. A single layer represents one
  * index in a crease-pattern.
  *
  * @param shapes the distinct segments of paper in the layer
  * @param folds a map of lines in the layer to fold types
  */
case class PaperLayer(private val shapes: Set[Polygon], private val folds: Map[Line, FoldType]) {
  require(shapes.nonEmpty, "Paper layer must have some content.")

  /**
    * Segments a layer along a given line into two new [[PaperLayer]]s.
    * Sometimes a segment line will not split any shapes, in this case
    * no additional layer is produced for that side of the layer.
    *
    * @param segmentLine the line to split the layer along
    * @return possibly two new paper layers, depending on the contents of the layer
    */
  def segmentOnLine(segmentLine: Line): (Option[PaperLayer], Option[PaperLayer]) = {
    // distinguish between which distinct shapes in the layer will be affected
    // by the slice and which won't be
    val (affectedShapes, unaffectedShapes) = shapes partition { shape =>
      (shape overlaps segmentLine.a) && (shape overlaps segmentLine.b)
    }

    // slice the affected shapes and separate them into new shapes
    val newShapes: Set[Polygon] = for {
      affectedShape <- affectedShapes

      (leftHand, rightHand) = affectedShape slice segmentLine
      shape <- Seq(leftHand, rightHand)

    } yield shape

    // convenience function for separating shapes by their
    // position in the layer
    def filterByPosition(positionFilter: Double => Boolean) =
      (newShapes ++ unaffectedShapes) filter (shape => positionFilter(shape compareTo segmentLine))

    // 'left' and 'right' are arbitrary identifiers and might not
    // correspond physically to left and right positions
    val left  = filterByPosition(_ < 0)
    val right = filterByPosition(_ > 0)

    // finally separate the line => fold-type mapping
    // so that each new layer has the correct fold map

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

    // currently segmented layers are modified to have
    // creased folds on the lines where the layer has been segmented
    // this is technically part of the fold process
    // but this seemed like a convenient place to do it

    val creasedLeftFolds  = leftFolds creaseFoldsAlong segmentLine
    val creasedRightFolds = rightFolds creaseFoldsAlong segmentLine

    // if segmenting the layer does not produce
    // a new layer on either side those respective sides
    // return nothing, ie None
    val leftLayer  = if (left.nonEmpty) Some(PaperLayer(left, creasedLeftFolds)) else None
    val rightLayer = if (right.nonEmpty) Some(PaperLayer(right, creasedRightFolds)) else None

    (leftLayer, rightLayer)
  }

  /**
    * Rotates the layer 180 degrees around the given line (as the axis)
    *
    * @param rotationLine the axis to rotate around
    * @return the flipped layer
    */
  def rotateAround(rotationLine: Line): PaperLayer = {
    val rotatedShapes = shapes map (_ flatMap (_ reflectedOver rotationLine))

    val rotatedFolds = folds map tupled((line, foldType) =>
      (line flatMap (_ reflectedOver rotationLine)) -> (foldType match {
        // when we rotate the layer, the viewing direction of the
        // folds in the pattern is flipped
        // therefore we must update all the fold assignments to reflect this
        case MountainFold    => ValleyFold
        case ValleyFold      => MountainFold
        case other: FoldType => other
      }))

    new PaperLayer(rotatedShapes, rotatedFolds)
  }

  /**
    * Merges the two layers together so that they become one [[PaperLayer]]
    *
    * Two layers are considered incompatible if they contain shapes that overlap
    * each other. Likewise two layers are considered compatible if none of their
    * shapes overlap.
    *
    * @param otherLayer the layer to merge with
    * @return if they can be merged, the merged layers
    */
  def mergeWith(otherLayer: PaperLayer): Option[PaperLayer] = {
    val canBeAddedSafely = !(shapes exists (shape => otherLayer.shapes exists (_ overlaps shape)))
    val allOnEdge = otherLayer.shapes forall (shape =>
      shapes exists (s => s.points forall shape.isOnEdge))

    if (canBeAddedSafely && !allOnEdge) {
      Some(PaperLayer(shapes ++ otherLayer.shapes, folds ++ otherLayer.folds))
    } else None
  }

  /**
    * Tests if the layer covers a given line.
    * A line is only considered covered if the line is ''inside'' the shape.
    *
    * @param line the line to test
    * @return if the line is inside the shape
    */
  def coversLine(line: Line): Boolean =
    shapes exists (shape => line map ((a, b) => coversPoint(a) || coversPoint(b)))

  /**
    * Tests if the layer covers a given point.
    * A point is only considered covered if the point is ''inside'' the shape.
    *
    * @param point the point to test
    * @return if the point is inside the shape
    */
  def coversPoint(point: Point): Boolean =
    shapes exists (shape => (shape overlaps point) && !(shape isOnEdge point))

  /**
    * Test if the given point is exclusively on the edge of any shape in the layer.
    *
    * @param point the point to test
    * @return if the point is exclusively on the edge of any shape in the layer
    */
  def isOnEdge(point: Point): Boolean = shapes exists (_ isOnEdge point)

  /**
    * Returns the total (sum) surface area of this layer.
    *
    * @return the total sum of the surface area of every shape in the layer
    */
  def surfaceArea: Double = (shapes map (_.surfaceArea)).sum

  /**
    * Returns the bounding box of the layer.
    * Used for checking a point sits within this layer or not.
    *
    * @return the bounding box of the layer
    */
  def boundingBox: Rectangle = (shapes map (_.boundingBox)) reduceLeft (_ combineWith _)

  /**
    * Check if the specified fold-line is part of this layer.
    *
    * @param foldLine the fold-line to search for
    * @return if the layer contains that fold-line
    */
  def contains(foldLine: Line): Boolean = folds contains foldLine

  /**
    * Slightly more explicit than [[contains(foldLine: FoldLine)]] in that
    * it checks if the current layer contains the fold-line AND if that
    * fold-line is assigned the desired fold-type.
    *
    * @param fold the fold to search the layer for
    * @return if the layer contains that fold
    */
  def contains(fold: Fold): Boolean =
    contains(fold.line) && (folds get fold.line contains fold.foldType)

  /**
    * Checks if there is a fold in the layer that matches a given predicate.
    *
    * @param predicate the predicate to check against
    * @return if there is a fold in the layer that matches the predicate
    */
  def exists(predicate: (Fold) => Boolean): Boolean = (foldable ++ unfoldable) exists predicate

  /**
    * @return all mountain folds in the layer
    */
  def mountainFolds: Set[Fold] = extractFolds(MountainFold)

  /**
    * @return all valley folds in the layer
    */
  def valleyFolds: Set[Fold] = extractFolds(ValleyFold)

  /**
    * @return all creased folds in the layer
    */
  def creasedFolds: Set[Fold] = extractFolds(CreasedFold)

  /**
    * @return all paper boundaries in the layer
    */
  def paperBoundaries: Set[Fold] = extractFolds(PaperBoundary)

  /**
    * Returns all [[MountainFold]]s or [[ValleyFold]]s
    *
    * @return all foldable folds in the layer
    */
  def foldable: Set[Fold] = mountainFolds ++ valleyFolds

  /**
    * Returns all [[CreasedFold]]s or [[PaperBoundary]]s
    *
    * @return all physical boundaries in the paper
    */
  def unfoldable: Set[Fold] = creasedFolds ++ paperBoundaries

  override def equals(other: Any): Boolean = other match {
    case PaperLayer(otherShapes, otherFolds) => shapes == otherShapes && folds == otherFolds
    case _                                   => false
  }

  override def hashCode(): Int = 17 * (31 + shapes.hashCode()) * (31 + folds.hashCode())

  override def toString: String = {
    folds map tupled((line, foldType) => line map ((a, b) => s"$a $foldType $b")) mkString "\n"
  }

  /**
    * Extracts folds of the given fold type from the map, and
    * converts them to convenient fold objects.
    *
    * @param foldType the fold type to filter by
    * @return
    */
  private def extractFolds(foldType: FoldType) = {
    val foldsOfType = folds filterByFoldType foldType
    foldsOfType.keySet map (line => Fold(line, folds(line)))
  }
}

/**
  * Companion object for [[PaperLayer]] class.
  */
object PaperLayer {

  /**
    * Creates a new [[PaperLayer]] in case-constructor format
    *
    * @param folds a var-args list of all folds in the layer
    * @return a new paper layer
    */
  def apply(folds: Fold*) = new PaperLayer(
    Set(new Polygon(folds.toSet flatMap { (fold: Fold) =>
      val points = fold.line.points
      Set(points._1, points._2)

    })),
    (for (fold <- folds) yield fold.line -> fold.foldType).toMap
  )

  /**
    * Provides useful operations on [[Map[Line, FoldType]], aka
    * a 'fold map'.
    *
    * This class is created implicitly when the methods are called
    * and should rarely be instantiated personally.
    *
    * @param self the fold map to operate on
    */
  implicit final class FoldMapOps(private val self: Map[Line, FoldType]) extends AnyVal {

    /**
      * Convert all folds along a given line to creased folds.
      *
      * @param foldLine definition of the line where everything should be creased folds
      * @return an updated fold map
      */
    def creaseFoldsAlong(foldLine: Line): Map[Line, FoldType] =
      self map tupled((line, foldType) =>
        if (line alignsWith foldLine) (line, CreasedFold) else (line, foldType))

    /**
      * Short-hand for filtering the fold map by a desired [[FoldType]].
      *
      * @param foldType the fold-type to filter by.
      * @return a filtered map
      */
    def filterByFoldType(foldType: FoldType): Map[Line, FoldType] =
      self.filterKeys(line => self(line) == foldType)
  }
}
