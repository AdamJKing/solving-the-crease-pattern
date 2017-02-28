package uk.ac.aber.adk15

import org.scalatest.{FlatSpec, Matchers}
import uk.ac.aber.adk15.paper.PaperModelPredef.BlankPaper
import uk.ac.aber.adk15.paper.{MountainFoldType, UnfoldedPaperEdge}

class PaperModelSpec extends FlatSpec with Matchers {

  "The paper model" should "be capable of being folded" in {
    val paperModel = BlankPaper

    paperModel.fold(UnfoldedPaperEdge(Point(0,0), Point(0, 100), MountainFoldType()))
  }
}
