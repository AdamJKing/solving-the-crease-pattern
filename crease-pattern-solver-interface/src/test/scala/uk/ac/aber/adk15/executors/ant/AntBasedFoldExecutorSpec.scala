package uk.ac.aber.adk15.executors.ant

import org.mockito.BDDMockito._
import org.mockito.Matchers.any
import org.mockito.Mock
import org.mockito.Mockito._
import uk.ac.aber.adk15.CommonAsyncSpec
import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.fold.Fold
import uk.ac.aber.adk15.paper.fold.Fold.Helpers._

class AntBasedFoldExecutorSpec extends CommonAsyncSpec {

  @Mock private var antTraverser: AntTraverser = _
  @Mock private var config: Config             = _

  private var antBasedFoldExecutor: AntBasedFoldExecutor = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    antBasedFoldExecutor = new AntBasedFoldExecutorImpl(antTraverser)
  }

  it should "eventually return the discovered fold order if one exists" in {
    // given
    val myFoldOrder     = List(Point(0, 0) /\ Point(10, 10), Point(0, 5) \/ Point(5, 5))
    val myCreasePattern = mock[CreasePattern]

    given(config.maxThreads) willReturn 8
    given(antTraverser traverseTree any[FoldNode]) willReturn Some(myFoldOrder)

    // when
    val futureFoldOrder = antBasedFoldExecutor findFoldOrder (myCreasePattern, 8)

    // then
    futureFoldOrder map { maybeFoldOrder =>
      maybeFoldOrder shouldBe defined
      maybeFoldOrder should have('value (myFoldOrder))
    }
  }

  "executing the traverser" should "only start as many threads as is allowed" in {
    // given
    val numThreads = 8
    given(config.maxThreads) willReturn 8
    given(antTraverser traverseTree any[FoldNode]) willReturn mock[Option[List[Fold]]]

    // when
    (antBasedFoldExecutor findFoldOrder (CreasePattern.empty, 8)) map { _ =>
      //then
      verify(antTraverser, times(numThreads)) traverseTree any[FoldNode]
      succeed
    }
  }
}
