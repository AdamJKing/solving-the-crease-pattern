package uk.ac.aber.adk15.paper

import com.typesafe.scalalogging.Logger

import scala.Function.tupled
import scala.annotation.tailrec
import scala.collection.mutable

/**
  * Represents a single fold-able Origami model.
  *
  * Model consists of layers which can be manipulated. It's worth noting that this
  * class performs no actual validation or verification of the folds being performed.
  * Instead to find out which folds are valid you should use the
  * [[uk.ac.aber.adk15.services.FoldSelectionService]].
  *
  * @param layers the layers of paper that form the paper model
  */
case class CreasePattern(layers: List[PaperLayer]) {
  import CreasePattern._

  private val logger = Logger[CreasePattern]

  /**
    * Returns a list of all remaining folds in the crease pattern,
    * regardless of which folds are currently legal.
    *
    * @return a list of all mountain/valley folds remaining in the crease pattern
    */
  def remainingFolds: Set[Fold] = {
    def extract = layers map (_: PaperLayer => Set[Fold]) reduce (_ ++ _)

    extract(_.mountainFolds) ++ extract(_.valleyFolds)
  }

  /**
    * Alters the crease-pattern such that the given fold is now folded. It will '''not'''
    * check if this crease is legal and instead will attempt the following;
    *
    *   - "slice" the layer stack in two based on the fold
    *   - only layers that either contain the fold or do not block the fold will be affected
    *   - the fold with the smaller surface area will be folded
    *   - the layers will then be rotated based on the fold type
    *   - finally the new layers will be "healed" with the old ones, this is neccessary if there
    *     are areas of paper that can happily exist on the same layer
    *
    * For more detailed information on how this algorithm works from a theoretical point of view
    * please consult the report/documentation.
    *
    * @param edge the edge to fold along
    * @return a new folded model
    */
  def fold(edge: Fold): CreasePattern = {
    validateLegalFold(edge)

    // tests if a given layer is affected by the edge we've been given
    def isFoldable(layer: PaperLayer) = (layer contains edge) || !(layer coversFold edge)

    val (affectedLayers, unaffectedLayers) = layers partition isFoldable
    val (leftSplit, rightSplit)            = (affectedLayers map (_ segmentOnFold edge)).unzip

    // finds the largest possible surface area covered by the given layers
    def largestSurfaceArea(layers: List[PaperLayer]) = (layers map (_.surfaceArea)).max

    val maximumAreaOfLeft  = largestSurfaceArea(leftSplit)
    val maximumAreaOfRight = largestSurfaceArea(rightSplit)

    // move the given layers on top of the other layers
    def foldLayersAbove(moveToTop: List[PaperLayer], moveToBottom: List[PaperLayer]) =
      (moveToTop map (_ rotateAround (edge.start, edge.end))) ++ moveToBottom

    // move the given layers beneath the other layers
    def foldLayersBelow(moveToTop: List[PaperLayer], moveToBottom: List[PaperLayer]) =
      moveToBottom ++ (moveToTop map (_ rotateAround (edge.start, edge.end)))

    // distinguish which way we want to fold our model
    // currently we prefer to fold the smaller side over the large side
    // as a smaller surface area implies less attached layers
    val (layersToFold, layersToLeave) = {
      if (maximumAreaOfLeft < maximumAreaOfRight)
        (leftSplit, rightSplit)
      else
        (rightSplit, leftSplit)
    }

    // position the new layers based on the fold type
    val newLayers = {
      if (edge.foldType == ValleyFold)
        foldLayersAbove(layersToFold, layersToLeave)
      else
        foldLayersBelow(layersToFold, layersToLeave)
    }

    new CreasePattern(repair(unaffectedLayers, newLayers, edge.foldType))
  }

  /**
    * Returns the number of layers in the crease-pattern.
    *
    * Useful for testing.
    *
    * @return the number of layers in the pattern
    */
  def size: Int = layers.length

  override def equals(obj: Any): Boolean = obj match {
    case other: CreasePattern => other.layers == this.layers
    case _                    => false
  }

  override def hashCode(): Int = 17 * 31 * layers.hashCode()

  override def toString = s"{\n${layers mkString ",\n\n\t"}\n}"

  /**
    * This is the only validation that we perform when folding, and that is
    * to check that the fold the user is attempting to fold actually exists or
    * is known of by the crease pattern.
    *
    * This check is especially useful for ensuring tests are accurate and there
    * are no erroneous false positives.
    *
    * @param fold the fold to validate
    */
  private def validateLegalFold(fold: Fold) =
    if (!(remainingFolds contains fold)) {
      logger error s"Could not fold $fold, folds available were $remainingFolds"
      throw new IllegalFoldException(fold)
    }
}

/**
  * Companion object containing utility functions for [[CreasePattern]] operations.
  *
  */
object CreasePattern {

  /**
    * Convenience function for creating a new [[CreasePattern]].
    *
    * @param paperLayers the layers in the crease pattern
    * @return a new crease pattern instance
    */
  def from(paperLayers: PaperLayer*): CreasePattern = {
    new CreasePattern(paperLayers.toList)
  }

  /**
    * Merge two distinct layer stacks together. Layers that do not define
    * conflicting regions will be merged together.
    *
    * Depending on the fold direction, the layers will be shifted along until
    * they can be merged successfully.
    *
    * For more information on this method please consult the documentation/report.
    *
    * @param old the 'old' layers are the ''subject'' of the merge
    * @param `new` the 'new' layers are the layers to ''be'' merged
    * @param foldType the type of the fold, determining the shift direction
    * @return
    */
  def repair(old: List[PaperLayer],
             `new`: List[PaperLayer],
             foldType: FoldType): List[PaperLayer] = {

    // the calculations involved in merging two layers are
    // generally expensive, so we store the results in a map
    // so we can access them later without having to recalculate them
    var mergedMap = mutable.Map[Int, List[Option[PaperLayer]]]()

    /**
      * Recursive algorithm to find the number of mutually compatible
      * (merge-able) layers in our two stacks of layers.
      *
      * @param crossover an integer value representing the number of elements to
      *                  merge from each stack (this is the crossover to be tested)
      * @return the final calculated crossover value (this is the crossover to be returned)
      */
    @tailrec
    def findCrossover(crossover: Int): Int = {
      // tests if the subject and the mergeCanidate can be merged without issue
      def isValidCrossover(subject: List[PaperLayer], mergeCandidate: List[PaperLayer]) = {
        val mergedLayers = subject zip mergeCandidate map tupled(_ mergeWith _)
        // update the cached merges
        mergedMap += (crossover -> mergedLayers)
        mergedLayers forall (_.isDefined)
      }

      val (base, other) = {
        if (foldType == ValleyFold)
          (old take crossover, `new` takeRight crossover)
        else
          (old takeRight crossover, `new` take crossover)
      }

      if (isValidCrossover(base, other)) crossover else findCrossover(crossover - 1)
    }

    // the largest possible crossover is the size of the smallest list
    val crossover = findCrossover(math.min(old.size, `new`.size))

    if (foldType == ValleyFold) {
      val (top, bottom) = (`new` dropRight crossover, old drop crossover)
      top ++ (mergedMap(crossover) map (_.get)) ++ bottom
    } else {
      val (top, bottom) = (old dropRight crossover, `new` drop crossover)
      top ++ (mergedMap(crossover) map (_.get)) ++ bottom
    }
  }
}
