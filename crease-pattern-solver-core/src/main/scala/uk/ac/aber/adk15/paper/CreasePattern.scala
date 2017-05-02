package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.paper.fold.PaperEdgeHelpers._
import uk.ac.aber.adk15.paper.fold.{Fold, FoldContext, FoldSelection, OngoingFold}

/**
  * Represents a single fold-able Origami model.
  *
  * TODO: out of date
  * Model consists of layers which can be manipulated. It's worth noting that this
  * class performs no actual validation or verification of the folds being performed.
  * Instead to find out which folds are valid you should use the
  * [[uk.ac.aber.adk15.services.FoldSelectionService]].
  *
  * @param paperLayers the layers of paper that form the paper model
  */
case class CreasePattern(private val paperLayers: List[PaperLayer]) {
  require(paperLayers.nonEmpty, "Crease pattern cannot have 0 layers")

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
    * @return a new folded model
    */
  def fold(fold: Fold): OngoingFold = {
    require(availableFolds contains fold,
            s"Fold is not possible fold=$fold availableFolds=$availableFolds creasePattern=$this")

    val foldContext = new FoldContext(this, fold)
    new OngoingFold(foldContext)
  }

  @inline final def <~~(fold: Fold): CreasePattern = (this fold fold).crease

  def layers: List[PaperLayer] = paperLayers

  def hasRemainingFolds: Boolean = (layers map (_.foldable)) forall (_.nonEmpty)

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
}

/**
  * Companion object containing utility functions for [[CreasePattern]] operations.
  *
  */
object CreasePattern {

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
