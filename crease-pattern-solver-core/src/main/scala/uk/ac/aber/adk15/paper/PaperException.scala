package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.paper.fold.Fold

abstract class PaperException(msg: String) extends Exception(msg)

class EdgeAlreadyCreasedException(edge: Fold)
    extends PaperException(s"Edge $edge already creased.")

class IllegalFoldException(edge: Fold) extends PaperException(s"Error with edge: $edge")
