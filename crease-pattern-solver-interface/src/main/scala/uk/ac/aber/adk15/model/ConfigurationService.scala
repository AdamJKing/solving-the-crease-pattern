package uk.ac.aber.adk15.model

trait ConfigurationService {
  def configuration: Config
  def configuration_=(config: Config)
}

class ConfigurationServiceImpl extends ConfigurationService {

  private var config = Config()

  override def configuration: Config                 = Config(config.maxThreads)
  override def configuration_=(config: Config): Unit = { this.config = config }

}
