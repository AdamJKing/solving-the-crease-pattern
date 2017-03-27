package uk.ac.aber.adk15.executors.ant

import org.mockito.Mock
import uk.ac.aber.adk15.CommonFlatSpec

class AntTraverserSpec extends CommonFlatSpec {

  @Mock private var diceRollService: DiceRollService = _

  private var antTraverser: AntTraverser = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    antTraverser = new AntTraverserImpl(diceRollService)
  }

  it should "" in {
    // given

  }
}
