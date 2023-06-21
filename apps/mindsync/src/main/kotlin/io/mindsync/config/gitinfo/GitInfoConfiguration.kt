package io.mindsync.config.gitinfo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.ClassPathResource


@Configuration
internal class GitInfoConfiguration {
  @Bean
  fun placeholderConfigurer(): PropertySourcesPlaceholderConfigurer {
    val propsConfig = PropertySourcesPlaceholderConfigurer()
    propsConfig.setLocation(ClassPathResource("git.properties"))
    propsConfig.setIgnoreResourceNotFound(true)
    propsConfig.setIgnoreUnresolvablePlaceholders(true)
    return propsConfig
  }
}

