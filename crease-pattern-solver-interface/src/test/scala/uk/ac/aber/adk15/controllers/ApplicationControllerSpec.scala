package uk.ac.aber.adk15.controllers

import org.mockito.BDDMockito._
import org.mockito.Matchers._
import org.mockito.Mock
import org.mockito.Mockito._
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.executors.ant.AntBasedFoldExecutor
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.fold.Fold

import scala.concurrent.{ExecutionContext, Future}

class ApplicationControllerSpec extends CommonFlatSpec {

  @Mock private var antBasedFoldExecutor: AntBasedFoldExecutor = _
  @Mock private var config: Config                             = _

  private var applicationController: ApplicationController = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    applicationController = new ApplicationControllerImpl(antBasedFoldExecutor)

    given(config.maxThreads) willReturn 8
  }

  "When executing the controller" should "use the values from the configuration" in {
    // given
    val creasePattern = mock[CreasePattern]
    given(
      antBasedFoldExecutor
        .findFoldOrder(any[CreasePattern], anyInt)(any[ExecutionContext]))
      .willReturn(mock[Future[Option[List[Fold]]]])

    // when
    applicationController execute (creasePattern, config)

    // then
    verify(config, times(2)).maxThreads
  }

  it should "successfully find a fold order if there are no issues" in {
    // given
    given(
      antBasedFoldExecutor
        .findFoldOrder(any[CreasePattern], anyInt)(any[ExecutionContext]))
      .willReturn(mock[Future[Option[List[Fold]]]])

    // when
    applicationController execute (mock[CreasePattern], config)

    // then
    verify(antBasedFoldExecutor).findFoldOrder(any[CreasePattern], anyInt)(any[ExecutionContext])
  }

  it should "throw an exception if one occurs during the execution" in {
    // given
    val deadFuture = Future.failed(new IllegalArgumentException)
    given(
      antBasedFoldExecutor
        .findFoldOrder(any[CreasePattern], anyInt)(any[ExecutionContext])) willReturn deadFuture

    // then
    applicationController execute (mock[CreasePattern], config) shouldBe deadFuture
  }
}
