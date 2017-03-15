package uk.ac.aber.adk15.model

import uk.ac.aber.adk15.model.ConfigurationConstants._

object ConfigurationConstants {
  val MaxThreadsDefault = 8
  val MaxThreadsMin     = 0
  val MaxThreadsMax     = 256
}

sealed case class Config(_maxThreads: Int = MaxThreadsDefault) {

  def maxThreads: Int = _maxThreads
  def maxThreads_=(maxThreads: Int): Int = maxThreads match {
    case _ if maxThreads > MaxThreadsMax => MaxThreadsMax
    case _ if maxThreads < MaxThreadsMin => MaxThreadsMin
    case _                               => maxThreads
  }

}

trait ConfigurationService {
  def configuration: Config
  def configuration_=(config: Config)
}

class ConfigurationServiceImpl extends ConfigurationService {

  private var config = Config()

  override def configuration: Config                 = Config(config.maxThreads)
  override def configuration_=(config: Config): Unit = { this.config = config }

}
