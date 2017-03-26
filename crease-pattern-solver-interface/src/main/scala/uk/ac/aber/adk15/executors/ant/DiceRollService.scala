package uk.ac.aber.adk15.executors.ant

import scala.util.Random

trait DiceRollService {
  def randomDiceRoll(floor: Double, ceil: Double): Double
  def randomWeightedDiceRoll(weights: List[Int]): Int
}

class DiceRollServiceImpl extends DiceRollService {

  override def randomDiceRoll(floor: Double, ceil: Double): Double =
    floor + ((Random nextDouble) * ((ceil - floor) + 1.0))

  override def randomWeightedDiceRoll(weights: List[Int]): Int = {
    if (weights.size == 1 || weights.isEmpty) return 0

    lazy val probabilities = weights map (_ / weights.sum)

    var cumulativeProp = 0.0
    val diceRoll       = Random.nextDouble()

    Stream.range(0, weights.length) find ((c: Int) => {

      // an infinite value would arbitrarily trigger the condition,
      // we want to prevent any item from having an infinite chance of occurring
      diceRoll <= cumulativeProp && !cumulativeProp.isInfinite
      cumulativeProp += probabilities(c)
      diceRoll <= cumulativeProp

    }) getOrElse randomDiceRoll(0, weights.length).toInt
  }
}
