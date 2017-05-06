package uk.ac.aber.adk15.paper.fold

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
sealed abstract class FoldType {}

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
  override def toString = "--"
}
