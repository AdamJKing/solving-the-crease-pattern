package uk.ac.aber.adk15.executors

import uk.ac.aber.adk15.CommonSpec
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.model.ConfigConstants.ExecutorType

class FoldExecutorFactorySpec extends CommonSpec {

  private var foldExecutorFactory: FoldExecutorFactory = _

  // Test data
  private val DesiredExecutorType = ExecutorType.PreDesignated
  private val MaxThreads          = 8
  private val ExampleConfig       = Config(DesiredExecutorType, MaxThreads)

  override def beforeEach(): Unit = {
    super.beforeEach()
    foldExecutorFactory = new FoldExecutorFactoryImpl
  }

  "Factory" should "construct new instance of desired factory type" in {
    // when
    val factory = foldExecutorFactory.createFactoryFrom(ExampleConfig)

    // then
    factory shouldBe a[PreDesignatedFoldExecutor]
  }
}
