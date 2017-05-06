package uk.ac.aber.adk15.executors.ant

import org.mockito.BDDMockito._
import org.mockito.Matchers._
import org.scalatest.Inside
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.fold.Fold.Helpers._
import uk.ac.aber.adk15.paper.fold.{Fold, OngoingFold}

class FoldNodeSpec extends CommonFlatSpec with Inside {

  it should "correctly generate all children" in {
    // given
    val creasePattern = mock[CreasePattern]
    val children      = Set(Point(0, 0) /\ Point(10, 10))
    val ongoingFold   = mock[OngoingFold]

    val newCreasePattern = mock[CreasePattern]

    given(creasePattern.availableFolds) willReturn children
    given(creasePattern fold any[Fold]) willReturn ongoingFold
    given(ongoingFold.crease) willReturn newCreasePattern

    // when
    val operationNode = FoldNode(creasePattern, None)

    // then
    operationNode.children should contain(FoldNode(newCreasePattern, Some(children.head)))
  }
}
