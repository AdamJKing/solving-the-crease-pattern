package uk.ac.aber.adk15.paper.newapi

import com.typesafe.scalalogging.Logger

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

  private val logger = Logger[NewCreasePattern]

  def availableFolds: Set[NewFold] = new FoldSelection(layers).getAvailableOperations

//  /**
//    * Returns a list of all remaining folds in the crease pattern,
//    * regardless of which folds are currently legal.
//    *
//    * @return a list of all mountain/valley folds remaining in the crease pattern
//    */
//  @deprecated
//  def remainingFolds: Map[Line, FoldType] = {
//    def extract = layers map (_: NewPaperLayer => Set[Line]) reduce (_ ++ _)
//
//    val mountainFolds = for (line <- extract(_.mountainFolds)) yield line -> MountainFold
//    val valleyFolds   = for (line <- extract(_.valleyFolds)) yield line   -> ValleyFold
//
//    (mountainFolds ++ valleyFolds).toMap
//  }

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
  def fold(fold: NewFold): OngoingFold = {
    val foldContext = new FoldContext(this, fold)
    new OngoingFold(foldContext)
  }

  @inline final def <~~(fold: NewFold): NewCreasePattern = {
    this.fold(fold).crease
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

//  /**
//    * This is the only validation that we perform when folding, and that is
//    * to check that the fold the user is attempting to fold actually exists or
//    * is known of by the crease pattern.
//    *
//    * This check is especially useful for ensuring tests are accurate and there
//    * are no erroneous false positives.
//    *
//    * @param fold the fold to validate
//    */
//  private def validateLegalFold(fold: NewFold) =
//    if (!((remainingFolds contains fold.line) && remainingFolds(fold.line) == fold.foldType)) {
//      logger error s"Could not fold $fold, folds available were $remainingFolds"
//      throw new IllegalArgumentException(fold.toString)
//    }
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
}
