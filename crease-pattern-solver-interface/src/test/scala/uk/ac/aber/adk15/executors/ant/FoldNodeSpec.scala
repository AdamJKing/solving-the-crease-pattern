package uk.ac.aber.adk15.executors.ant

import org.mockito.BDDMockito._
import org.scalatest.Inside
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._
import uk.ac.aber.adk15.services.FoldSelectionService

class FoldNodeSpec extends CommonFlatSpec with Inside {

  private implicit var foldSelectionService = mock[FoldSelectionService]

  it should "correctly generate all children" in {
    // given
    val myCreasePattern = mock[CreasePattern]
    val myChildren      = Set(Point(0, 0) /\ Point(10, 10))

    given(foldSelectionService getAvailableOperations myCreasePattern) willReturn myChildren

    // when
    val operationNode = FoldNode(myCreasePattern, None)

    // then
    every(operationNode.children) shouldBe a[FoldNode]
  }
}
