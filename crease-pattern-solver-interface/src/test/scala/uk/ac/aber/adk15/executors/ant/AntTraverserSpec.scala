package uk.ac.aber.adk15.executors.ant

import org.mockito.Mock
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.view.EventBus

class AntTraverserSpec extends CommonFlatSpec {

  @Mock private var diceRollService: DiceRollService = _
  @Mock private var eventBus: EventBus[AntTraversalEvent] = _

  private var antTraverser: AntTraverser = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    antTraverser = new AntTraverserImpl(diceRollService, eventBus)
  }

  it should "" in {
    // given

  }
}
