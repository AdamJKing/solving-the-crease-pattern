package uk.ac.aber.adk15.paper

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

  private def this(nodes: Product, foldType: FoldType) {
    this(nodes.productElement(0).asInstanceOf[N],
         nodes.productElement(1).asInstanceOf[N],
         foldType)
  }

  override def isValidCustom: Boolean = start != end

  override def isValidCustomExceptionMessage: String =
    super.isValidCustomExceptionMessage +
      "Edge has no length, start and end cannot be the same."

  override def canEqual(that: Any): Boolean =
    super.canEqual(that) &&
      (that.isInstanceOf[PaperEdge[N]] || this.getClass.isAssignableFrom(that.getClass))

  override def equals(that: Any): Boolean = {
    canEqual(that) && {
      val edge = that.asInstanceOf[PaperEdge[N]]

      val matchesStart = map(_ == edge.start) reduce (_ ^ _)
      val matchesEnd   = map(_ == edge.end) reduce (_ ^ _)

      matchesStart && matchesEnd && foldType == edge.foldType
    }
  }

  override def copy[NN](newNodes: Product) = new PaperEdge[NN](newNodes, foldType)

  override def toString = s"$start $foldType $end"
}

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
