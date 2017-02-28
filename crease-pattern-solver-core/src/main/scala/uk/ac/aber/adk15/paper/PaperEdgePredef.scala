package uk.ac.aber.adk15.paper

/**
  * Contains implicit definitions of operations as
  * shorthand for describing folds in a crease pattern.
  *
  * ie. Point(0, 0) /\ Point(10, 10) would be a mountain
  * fold between two points.
  *
  */
object PaperEdgePredef {
  final implicit class PaperEdgeAssoc[N](val start: N) extends AnyVal {
    @inline def /\(end: N) = new UnfoldedPaperEdge[N](start, end, MountainFoldType())
    @inline def \/(end: N) = new UnfoldedPaperEdge[N](start, end, ValleyFoldType())
  }
}
