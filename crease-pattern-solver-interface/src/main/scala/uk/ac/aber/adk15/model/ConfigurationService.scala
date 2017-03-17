package uk.ac.aber.adk15.model

import uk.ac.aber.adk15.model.ConfigurationConstants.ExecutorType.ExecutorType
import uk.ac.aber.adk15.model.ConfigurationConstants._

object ConfigurationConstants {
  val MaxThreadsDefault = 8
  val MaxThreadsMin     = 0
  val MaxThreadsMax     = 256

  object ExecutorType extends Enumeration {
    type ExecutorType = Value
    val PreDesignated = Value("PreDesignated")
  }
}

sealed case class Config(private var _foldFinderType: ExecutorType = ExecutorType.PreDesignated,
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

trait ConfigurationService {
  def configuration: Config
  def configuration_=(config: Config)
}

class ConfigurationServiceImpl extends ConfigurationService {

  private var config = Config()

  override def configuration: Config                 = Config(config.foldFinderType, config.maxThreads)
  override def configuration_=(config: Config): Unit = { this.config = config }

}
