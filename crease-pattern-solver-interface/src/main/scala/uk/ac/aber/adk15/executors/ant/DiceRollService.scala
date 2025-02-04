package uk.ac.aber.adk15.executors.ant

import scala.util.{Random, Try}

trait DiceRollService {
  def randomDiceRoll(floor: Double, ceil: Double): Double
  def randomWeightedDiceRoll(weights: List[Int]): Int
}

class DiceRollServiceImpl extends DiceRollService {

  override def randomDiceRoll(floor: Double, ceil: Double): Double =
    floor + ((Random nextDouble) * ((ceil - floor) + 1.0))

  override def randomWeightedDiceRoll(weights: List[Int]): Int = {
    if (weights.size == 1 || weights.isEmpty) return 0

    lazy val probabilities = weights map (w => Try(w / weights.sum) getOrElse 0)

    var cumulativeProp = 0.0
    val diceRoll       = Random.nextDouble()

    Stream.range(0, weights.length - 1) find ((c: Int) => {

      // an infinite value would arbitrarily trigger the condition,
      // we want to prevent any item from having an infinite chance of occurring
      diceRoll <= cumulativeProp && !cumulativeProp.isInfinite
      cumulativeProp += probabilities(c)
      diceRoll <= cumulativeProp

    }) getOrElse randomDiceRoll(0, weights.length - 1).toInt
  }
}
