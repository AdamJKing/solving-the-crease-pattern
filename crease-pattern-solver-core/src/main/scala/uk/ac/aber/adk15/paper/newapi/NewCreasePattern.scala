package uk.ac.aber.adk15.paper.newapi

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.geometry.Line
import uk.ac.aber.adk15.paper.{FoldType, MountainFold, ValleyFold}

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
  * @param _layers the layers of paper that form the paper model
  */
case class NewCreasePattern(private val _layers: List[NewPaperLayer]) {
  import NewCreasePattern._

  private val logger = Logger[NewCreasePattern]

  /**
    * Returns a list of all remaining folds in the crease pattern,
    * regardless of which folds are currently legal.
    *
    * @return a list of all mountain/valley folds remaining in the crease pattern
    */
  def remainingFolds: Map[Line, FoldType] = {
    def extract = layers map (_: NewPaperLayer => Set[Line]) reduce (_ ++ _)

    val mountainFolds = for (line <- extract(_.mountainFolds)) yield line -> MountainFold
    val valleyFolds   = for (line <- extract(_.valleyFolds)) yield line   -> ValleyFold

    (mountainFolds ++ valleyFolds).toMap
  }

  /**
    * Alters the crease-pattern such that the given fold is now folded. It will '''not'''
    * check if this crease is legal and instead will attempt the following;
    *
    *   - "slice" the layer stack in two based on the fold
    *   - only layers that either contain the fold or do not block the fold will be affected
    *   - the fold with the smaller surface area will be folded
    *   - the layers will then be rotated based on the fold type
    *   - finally the new layers will be "healed" with the old ones, this is necessary if there
    *     are areas of paper that can happily exist on the same layer
    *
    * For more detailed information on how this algorithm works from a theoretical point of view
    * please consult the report/documentation.
    *
    * @param fold the edge to fold along
    * @return a new folded model
    */
  def crease(fold: NewFold): NewCreasePattern = {
    validateLegalFold(fold)

    val foldContext   = new FoldContext(this, fold)
    val rotatedLayers = foldContext.layersToFold map (_ rotateAround fold.line)

    // position the new layers based on the fold type
    val newLayers = {
      if (fold.foldType == ValleyFold)
        rotatedLayers ++ foldContext.layersToLeave
      else
        foldContext.layersToLeave ++ rotatedLayers
    }

    val repairedLayers = repair(foldContext.unaffectedLayers, newLayers, fold.foldType)
    new NewCreasePattern(repairedLayers)
  }

  def layers: List[NewPaperLayer] = _layers

  /**
    * Returns the number of layers in the crease-pattern.
    *
    * Useful for testing.
    *
    * @return the number of layers in the pattern
    */
  def size: Int = layers.length

  override def equals(obj: Any): Boolean = obj match {
    case other: NewCreasePattern => other.layers == this.layers
    case _                       => false
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
  private def validateLegalFold(fold: NewFold) =
    if (!(remainingFolds contains fold.line)) {
      logger error s"Could not fold $fold, folds available were $remainingFolds"
      throw new IllegalArgumentException(fold.toString)
    }
}

/**
  * Companion object containing utility functions for [[NewCreasePattern]] operations.
  *
  */
object NewCreasePattern {

  /**
    * Convenience function for creating a new [[NewCreasePattern]].
    *
    * @param paperLayers the layers in the crease pattern
    * @return a new crease pattern instance
    */
  def from(paperLayers: NewPaperLayer*): NewCreasePattern = {
    new NewCreasePattern(paperLayers.toList)
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
  def repair(old: List[NewPaperLayer],
             `new`: List[NewPaperLayer],
             foldType: FoldType): List[NewPaperLayer] = {

    // the calculations involved in merging two layers are
    // generally expensive, so we store the results in a map
    // so we can access them later without having to recalculate them
    var mergedMap = mutable.Map[Int, List[Option[NewPaperLayer]]]()

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
      // tests if the subject and the mergeCandidate can be merged without issue
      def isValidCrossover(subject: List[NewPaperLayer], mergeCandidate: List[NewPaperLayer]) = {
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
