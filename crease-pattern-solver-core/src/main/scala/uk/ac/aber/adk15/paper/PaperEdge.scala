package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.Point

import scalax.collection.GraphEdge.{EdgeCopy, NodeProduct, UnDiEdge}
import scalax.collection.GraphPredef.OuterEdge

/**
  * A class for representing to relationship between two points on a piece of paper.
  *
  * This line is directionless, meaning `start` and `end` can be swapped arbitrarily.
  *
  */
case class PaperEdge[+N](start: N, end: N, foldType: FoldType)
    extends UnDiEdge[N](NodeProduct(start, end))
    with OuterEdge[N, PaperEdge]
    with EdgeCopy[PaperEdge] {

  /**
    * Custom constructor for internal usage by the graph library.
    *
    * @param nodes the nodes of the edge
    * @param foldType the fold type of the edge
    */
  private def this(nodes: Product, foldType: FoldType) {
    this(nodes.productElement(0).asInstanceOf[N],
         nodes.productElement(1).asInstanceOf[N],
         foldType)
  }

  /**
    * A customised validation method used by the graph library.
    *
    * Currently we only check that the paper edge has some length.
    *
    * @return if the edge is a valid paper edge
    */
  override def isValidCustom: Boolean = start != end

  /**
    * Customised exception message used when our previous validation method fails.
    *
    * @return the exception message
    */
  override def isValidCustomExceptionMessage: String =
    super.isValidCustomExceptionMessage +
      " Edge has no length, start and end cannot be the same."

  /**
    * Check that two edges are equal, the start and edge nodes should be interchangeable
    * so this method is directionless when it compares if two edges are the same.
    *
    * @param that the object to check for equality
    * @return if they are the same paper edge
    */
  override def equals(that: Any): Boolean = that match {
    case paperEdge: PaperEdge[N] =>
      paperEdge.toSet == this.toSet && paperEdge.foldType == this.foldType
    case _ => false
  }

  /**
    * Method for creating copies of edges required by trait `EdgeCopy`.
    *
    * @param newNodes the new nodes to give to the edge
    * @tparam NN the type of the nodes
    * @return the new, copied edge
    */
  override def copy[NN](newNodes: Product) = new PaperEdge[NN](newNodes, foldType)

  override def toString = s"$start $foldType $end"

  def crease: PaperEdge[N] = foldType match {
    case PaperBoundary             => this
    case MountainFold | ValleyFold => new PaperEdge[N](start, end, CreasedFold)
    case CreasedFold               => throw IllegalCreaseException(this)
  }
}

/**
  * This trait is the top level class for all possible types of fold.
  * For simplicity and easier programming we also consider hard edges to be
  * part of this.
  *
  * - Mountain / Valley fold: a fold indicator with a direction
  * - Creased fold: A mountain / valley fold that has been creased
  * - Paper boundary: This is a physical feature of the paper, ie, the *actual* edge of the paper
  *
  */
sealed trait FoldType

case object MountainFold extends FoldType {
  override def toString = "/\\"
}

case object ValleyFold extends FoldType {
  override def toString = "\\/"
}

case object CreasedFold extends FoldType {
  override def toString = "~~"
}

case object PaperBoundary extends FoldType {
  override def toString = "---"
}

/**
  * Contains implicit definitions of operations as
  * shorthand for describing folds in a crease pattern.
  *
  * ie. Point(0, 0) /\ Point(10, 10) would be a mountain
  * fold between two points.
  *
  */
object PaperEdgeHelpers {
  final implicit class PaperEdgeAssoc(val start: Point) extends AnyVal {
    @inline def /\(end: Point) = PaperEdge(start, end, MountainFold)
    @inline def \/(end: Point) = PaperEdge(start, end, ValleyFold)
    @inline def ~~(end: Point) = PaperEdge(start, end, CreasedFold)
    @inline def --(end: Point) = PaperEdge(start, end, PaperBoundary)
  }
}
