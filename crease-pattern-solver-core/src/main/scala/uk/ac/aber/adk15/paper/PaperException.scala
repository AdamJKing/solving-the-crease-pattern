package uk.ac.aber.adk15.paper

abstract class PaperException(msg: String) extends Exception(msg)

case class EdgeAlreadyCreasedException[N](edge: PaperEdge[N])
    extends PaperException(s"Edge $edge already creased.")

case class IllegalCreaseException[N](edge: PaperEdge[N])
    extends PaperException(s"Error with edge: $edge")
