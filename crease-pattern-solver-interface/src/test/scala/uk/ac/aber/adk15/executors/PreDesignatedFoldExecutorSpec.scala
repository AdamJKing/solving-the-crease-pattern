package uk.ac.aber.adk15.executors

import uk.ac.aber.adk15.paper.CreasePatternPredef.Constants.ModelConstants.BlankPaper
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._
import uk.ac.aber.adk15.{CommonSpec, Point}

class PreDesignatedFoldExecutorSpec extends CommonSpec {

  private var preDesignatedFoldExecutor: PreDesignatedFoldExecutor = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    preDesignatedFoldExecutor = new PreDesignatedFoldExecutor
  }

  "A predesignated executor" should "return a specific crease pattern and execute specific folds" in {
    // when
    val foldOrder = preDesignatedFoldExecutor findFoldOrder BlankPaper

    // then
    foldOrder should be {
      List(Point(50, 50) \/ Point(100, 100),
           Point(0, 50) /\ Point(25, 25),
           Point(0, 100) \/ Point(50, 50))
    }
  }
}
