package uk.ac.aber.adk15.controllers

import org.mockito.BDDMockito._
import org.mockito.Matchers._
import org.mockito.Mock
import org.mockito.Mockito._
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.executors.ant.AntBasedFoldExecutor
import uk.ac.aber.adk15.model.ConfigConstants.DefaultConfig
import uk.ac.aber.adk15.model.ConfigurationService
import uk.ac.aber.adk15.paper.{CreasePattern, Fold}

import scala.concurrent.{ExecutionContext, Future}

class ApplicationControllerSpec extends CommonFlatSpec {

  @Mock private var configurationService: ConfigurationService = _
  @Mock private var antBasedFoldExecutor: AntBasedFoldExecutor = _

  private var applicationController: ApplicationController = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    applicationController =
      new ApplicationControllerImpl(configurationService, antBasedFoldExecutor)

    given(configurationService.configuration) willReturn DefaultConfig
  }

  it should "use the configuration from the config service" in {
    // given
    given(
      antBasedFoldExecutor
        .findFoldOrder(any[CreasePattern])(implicitly(any[ExecutionContext])))
      .willReturn(mock[Future[Option[List[Fold]]]])

    // when
    applicationController.execute()

    // then
    verify(configurationService).configuration
  }

  it should "successfully find a fold order if there are no issues" in {
    // given
    given(
      antBasedFoldExecutor
        .findFoldOrder(any[CreasePattern])(implicitly(any[ExecutionContext])))
      .willReturn(mock[Future[Option[List[Fold]]]])

    // when
    applicationController.execute()

    // then
    verify(antBasedFoldExecutor).findFoldOrder(any[CreasePattern])(
      implicitly(any[ExecutionContext]))
  }

  it should "throw an exception if one occurs during the execution" in {
    // given
    val deadFuture = Future.failed(new IllegalArgumentException)
    given((antBasedFoldExecutor findFoldOrder any[CreasePattern])(any[ExecutionContext])) willReturn deadFuture

    // then
    applicationController.execute() shouldBe deadFuture
  }
}
