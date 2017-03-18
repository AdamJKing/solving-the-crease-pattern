package uk.ac.aber.adk15.controllers

import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito._
import org.mockito.Mock
import org.mockito.Mockito._
import uk.ac.aber.adk15.CommonSpec
import uk.ac.aber.adk15.executors.{FoldExecutor, FoldExecutorFactory}
import uk.ac.aber.adk15.model.ConfigurationConstants.DefaultConfig
import uk.ac.aber.adk15.model.{Config, ConfigurationService}
import uk.ac.aber.adk15.paper.CreasePattern

class ApplicationControllerSpec extends CommonSpec {

  @Mock private var configurationService: ConfigurationService = _
  @Mock private var foldExecutorFactory: FoldExecutorFactory   = _
  @Mock private var foldExecutor: FoldExecutor                 = _

  private var applicationController: ApplicationController = _

  override def beforeEach(): Unit = {
    super.beforeEach()

    given(foldExecutor findFoldOrder any[CreasePattern]) willReturn List.empty
    given(foldExecutorFactory createFactoryFrom any[Config]) willReturn foldExecutor

    applicationController =
      new ApplicationControllerImpl(configurationService, foldExecutorFactory)
  }

  "When started the application" should "use the configuration from the config service" in {
    // given
    given(configurationService.configuration) willReturn DefaultConfig

    // when
    applicationController.start()

    // then
    verify(configurationService).configuration
  }

  "When started the application" should "generate the appropriate executor from the factory" in {
    // given
    given(configurationService.configuration) willReturn DefaultConfig

    // when
    applicationController.start()

    // then
    val configCaptor = captor[Config]
    verify(foldExecutorFactory) createFactoryFrom (configCaptor capture)
    configCaptor.getValue shouldBe DefaultConfig
  }

  "The application" should "successfully find a fold order if there are no issues" in {
    // given
    given(foldExecutor findFoldOrder any[CreasePattern]) willReturn List.empty

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
