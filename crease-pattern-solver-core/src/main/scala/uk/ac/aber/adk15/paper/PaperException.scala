package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.Point

abstract class PaperException(msg: String) extends Exception(msg)

case class EdgeAlreadyCreasedException(edge: PaperEdge[Point])
    extends PaperException(s"Edge $edge already creased.")

case class IllegalCreaseException(edge: PaperEdge[Point])
    extends PaperException(
      s"Edge $edge was not found in crease pattern, or you are trying to fold along a paper boundary.")
