package uk.ac.aber.adk15.controllers

import java.io.File

import org.mockito.BDDMockito._
import org.mockito.Matchers._
import org.mockito.Mock
import org.mockito.Mockito._
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.executors.ant.AntBasedFoldExecutor
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.paper.{CreasePattern, Fold}

import scala.concurrent.{ExecutionContext, Future}

class ApplicationControllerSpec extends CommonFlatSpec {

  @Mock private var antBasedFoldExecutor: AntBasedFoldExecutor = _
  @Mock private var creasePatternParser: CreasePatternParser   = _
  @Mock private var config: Config                             = _

  private var applicationController: ApplicationController = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    applicationController =
      new ApplicationControllerImpl(antBasedFoldExecutor, creasePatternParser)

    given(config.maxThreads) willReturn 8
  }

  "When executing the controller" should "use the values from the configuration" in {
    // given
    val creasePatternFile = mock[File]
    given(
      antBasedFoldExecutor
        .findFoldOrder(any[CreasePattern], anyInt)(any[ExecutionContext]))
      .willReturn(mock[Future[Option[List[Fold]]]])
    given(creasePatternParser parseFile creasePatternFile) willReturn mock[CreasePattern]

    // when
    applicationController.execute(creasePatternFile, config)

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
    applicationController.execute(mock[File], config)

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
    applicationController.execute(mock[File], config) shouldBe deadFuture
  }

  it should "use the crease pattern parsed from the crease pattern file" in {
    // given
    val creasePatternFile = mock[File]
    given(
      antBasedFoldExecutor
        .findFoldOrder(any[CreasePattern], anyInt)(any[ExecutionContext]))
      .willReturn(mock[Future[Option[List[Fold]]]])

    // when
    applicationController.execute(creasePatternFile, config)

    // then
    verify(creasePatternParser) parseFile creasePatternFile
  }
}
