package uk.ac.aber.adk15.model

import uk.ac.aber.adk15.model.ConfigConstants._

object ConfigConstants {

  val MaxThreadsDefault = 8
  val MaxThreadsMin     = 0
  val MaxThreadsMax     = 256

  val DefaultConfig = Config(MaxThreadsDefault)
}

sealed case class Config(private var _maxThreads: Int = MaxThreadsDefault) {

  _maxThreads = validMaxThreadsFrom(_maxThreads)

  def maxThreads: Int                     = _maxThreads
  def maxThreads_=(maxThreads: Int): Unit = validMaxThreadsFrom(maxThreads)

  private def validMaxThreadsFrom(maxThreads: Int): Int = maxThreads match {
    case _ if maxThreads > MaxThreadsMax => MaxThreadsMax
    case _ if maxThreads < MaxThreadsMin => MaxThreadsMin
    case _                               => maxThreads
  }
}
