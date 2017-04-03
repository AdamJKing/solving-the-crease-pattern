package uk.ac.aber.adk15.model

import uk.ac.aber.adk15.model.Config.Constants._

case class Config(maxThreads: Int = MaxThreadsDefault) {
  require(maxThreads < MaxThreadsMax, "Max threads exceeds maximum allowed threads")
  require(maxThreads > MaxThreadsMin, "Min threads is below reasonable threshold.")
}

object Config {
  object Constants {

    val MaxThreadsDefault = 8
    val MaxThreadsMin     = 0
    val MaxThreadsMax     = 256

    val DefaultConfig = Config(MaxThreadsDefault)
  }
}
