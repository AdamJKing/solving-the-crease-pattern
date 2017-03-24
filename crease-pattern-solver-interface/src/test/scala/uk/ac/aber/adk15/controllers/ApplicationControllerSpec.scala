package uk.ac.aber.adk15.controllers

import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mock
import org.mockito.Mockito.verify
import uk.ac.aber.adk15.CommonSpec
import uk.ac.aber.adk15.executors.FoldExecutor
import uk.ac.aber.adk15.model.ConfigConstants.DefaultConfig
import uk.ac.aber.adk15.model.ConfigurationService
import uk.ac.aber.adk15.paper.{CreasePattern, Fold}
import uk.ac.aber.adk15.services.FoldSelectionService

import scala.concurrent.{ExecutionContext, Future}

class ApplicationControllerSpec extends CommonSpec {

  @Mock private var configurationService: ConfigurationService  = _
  @Mock private var foldExecutor: FoldExecutor                  = _
  @Mock private var foldSelectionService: FoldSelectionService  = _
  @Mock private implicit var executionContext: ExecutionContext = _

  @Mock private var futureList: Future[Option[List[Fold]]] = _

  private var applicationController: ApplicationController = _

  override def beforeEach(): Unit = {
    super.beforeEach()

    given(foldExecutor findFoldOrder any[CreasePattern]) willReturn futureList

    applicationController =
      new ApplicationControllerImpl(configurationService, foldSelectionService)
  }

  "When started the application" should "use the configuration from the config service" in {
    // given
    given(configurationService.configuration) willReturn DefaultConfig

    // when
    applicationController.start()

    // then
    verify(configurationService).configuration
  }

  "The application" should "successfully find a fold order if there are no issues" in {
    // given
    given(foldExecutor findFoldOrder any[CreasePattern]) willReturn futureList

    // when
    applicationController.start()

    // then
    // nothing to verify yet
  }

  "The application" should "throw an exception if one occurs during the execution" in {
    // given
    given(foldExecutor findFoldOrder any[CreasePattern]) willThrow new IllegalArgumentException

    // then
    intercept[Exception](applicationController.start()) shouldBe a[IllegalArgumentException]
  }
}
