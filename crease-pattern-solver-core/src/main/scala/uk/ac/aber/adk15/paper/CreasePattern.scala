package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.paper.fold.Fold.Helpers._
import uk.ac.aber.adk15.paper.fold.{Fold, FoldContext, FoldSelection, OngoingFold}

/**
  * Represents a single fold-able Origami model. A model must have 1
  * or more paper layers in order to be valid.
  *
  * @param paperLayers the layers of paper that form the paper model
  */
case class CreasePattern(private val paperLayers: List[PaperLayer]) {
  require(paperLayers.nonEmpty, "Crease pattern cannot have 0 layers")

  /**
    * Lists the folds that are currently considered 'foldable' in the crease-pattern
    *
    * @return all foldable folds in the crease-pattern
    */
  def availableFolds: Set[Fold] = new FoldSelection(layers).getAvailableOperations

  /**
    * Alters the crease-pattern such that the given fold is now folded. It will '''not'''
    * check if this crease is legal and instead will attempt the following;
    *
    *   - "slice" the layer stack in two based on the fold
    *   - only layers that either contain the fold or do not block the fold will be affected
    *   - the fold with the smaller surface area will be folded
    *   - the layers will then be rotated based on the fold type
    *   - finally the new layers will be "healed" with the old ones, this is necessary if there
    * are areas of paper that can happily exist on the same layer
    *
    * For more detailed information on how this algorithm works from a theoretical point of view
    * please consult the report/documentation.
    *
    * @param fold the edge to fold along
    * @return an on-going fold that must be creased
    */
  def fold(fold: Fold): OngoingFold = {
    require(availableFolds contains fold,
            s"Fold is not possible fold=$fold availableFolds=$availableFolds creasePattern=$this")

    val foldContext = new FoldContext(this, fold)
    new OngoingFold(foldContext)
  }

  /**
    * Convenience operator for applying a given fold.
    *
    * @usecase MODEL <~~ FOLD
    *
    * @param fold the edge to fold along
    * @return the new folded model
    */
  @inline final def <~~(fold: Fold): CreasePattern = (this fold fold).crease

  /**
    * @return the layers in the crease-pattern
    */
  def layers: List[PaperLayer] = paperLayers

  /**
    * Equivalent to `isEmpty`, tests if the crease-pattern has any
    * assigned creases left.
    *
    * Remaining folds are not necessarily foldable.
    *
    * @return the un-creased folds in the pattern
    */
  def hasRemainingFolds: Boolean = (layers map (_.foldable)) exists (_.nonEmpty)

  override def equals(obj: Any): Boolean = obj match {
    case other: CreasePattern => other.layers == this.layers
    case _                    => false
  }

  override def hashCode(): Int = 17 * 31 * layers.hashCode()

  override def toString = s"{\n${layers mkString ",\n\n\t"}\n}"
}

/**
  * Companion object containing utility functions for [[CreasePattern]] operations.
  *
  */
object CreasePattern {

  /**
    * Generates an empty crease-pattern, which is equivalent to
    * an empty piece of paper.
    *
    * @return blank crease-pattern
    */
  def empty: CreasePattern = CreasePattern from PaperLayer(
    Point(0, 0) -- Point(100, 0),
    Point(100, 0) -- Point(0, 100),
    Point(0, 100) -- Point(100, 100),
    Point(100, 100) -- Point(0, 0)
  )

  /**
    * Convenience function for creating a new [[CreasePattern]].
    *
    * @param paperLayers the layers in the crease pattern
    * @return a new crease pattern instance
    */
  def from(paperLayers: PaperLayer*): CreasePattern =
    new CreasePattern(paperLayers.toList)
}
