package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.Point

/**
  * Contains implicit definitions of operations as
  * shorthand for describing folds in a crease pattern.
  *
  * ie. Point(0, 0) /\ Point(10, 10) would be a mountain
  * fold between two points.
  *
  */
object PaperEdgePredef {
  final implicit class PaperEdgeAssoc(val start: Point) extends AnyVal {
    @inline def /\(end: Point) = PaperEdge(start, end, MountainFold)
    @inline def \/(end: Point) = PaperEdge(start, end, ValleyFold)
  }
}
