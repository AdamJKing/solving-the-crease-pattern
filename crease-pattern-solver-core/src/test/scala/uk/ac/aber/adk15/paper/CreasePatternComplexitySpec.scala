package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.paper.constants.FoldedModels._
import uk.ac.aber.adk15.paper.constants.UnfoldedCreasePatterns._

/**
  * [[CreasePattern]] complexity tests, used to validate
  * that the crease-pattern folds correctly with 'real' data.
  */
class CreasePatternComplexitySpec extends CommonFlatSpec {
  "A simple crease pattern" should "fold accurately and correctly" in {
    // given
    val creasePattern = SimpleCreasePattern

    // when
    val creasedModel = creasePattern <~~ SimpleFold

    // then
    creasedModel shouldBe SimpleFoldedModel
  }

  "A medium complexity crease pattern" should "fold accurately and correctly" in {
    // given
    val creasePattern = MediumComplexityCreasePattern

    // when
    val creasedModel = (creasePattern /: MediumFolds)(_ <~~ _)

    // then
    creasedModel shouldBe MediumComplexityModel
  }

  "When recognising the folds of a medium complexity crease pattern" should "correctly recognise all folds" in {
    // given
    val creasePattern = MediumComplexityCreasePattern

    // when
    val stages = MediumFolds.inits.toList map (folds => (creasePattern /: folds)(_ <~~ _))

    // then
    stages.last.availableFolds should contain(MediumFolds.head)
    stages(2).availableFolds should contain(MediumFolds(1))
    stages(1).availableFolds should contain(MediumFolds(2))
    stages.head.availableFolds should be(empty)
  }

  "Alternative: A medium complexity crease pattern" should "fold accurately and correctly" in {
    // given
    val creasePattern = MediumComplexityCreasePattern

    // when
    val creasedModel = (creasePattern /: AlternativeMediumFolds)(_ <~~ _)

    // then
    creasedModel shouldBe AlternativeMediumComplexityModel
  }
}
