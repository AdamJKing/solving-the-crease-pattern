package uk.ac.aber.adk15.model

import uk.ac.aber.adk15.model.ConfigurationConstants.ExecutorType.ExecutorType
import uk.ac.aber.adk15.model.ConfigurationConstants._

object ConfigurationConstants {

  object ExecutorType extends Enumeration {
    type ExecutorType = Value
    val PreDesignated = Value("PreDesignated")
  }

  val MaxThreadsDefault = 8
  val MaxThreadsMin     = 0
  val MaxThreadsMax     = 256

  val ExecutorTypeDefault = ExecutorType.PreDesignated

}

sealed case class Config(private var _foldFinderType: ExecutorType = ExecutorTypeDefault,
                         private var _maxThreads: Int = MaxThreadsDefault) {

  def maxThreads: Int = _maxThreads
  def maxThreads_=(maxThreads: Int): Unit = maxThreads match {
    case _ if maxThreads > MaxThreadsMax => _maxThreads = MaxThreadsMax
    case _ if maxThreads < MaxThreadsMin => _maxThreads = MaxThreadsMin
    case _                               => _maxThreads = maxThreads
  }

  def foldFinderType: ExecutorType = _foldFinderType
  def foldFinderType_=(foldFinderType: ExecutorType): Unit =
    _foldFinderType = foldFinderType
}
