package io.mindsync.config.gitinfo

import io.mindsync.common.domain.Generated
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.ClassPathResource

/**
 * Configuration class for loading Git information from git.properties file.
 *
 * @author Yuniel Acosta
 */
@Configuration
@Generated(reason = "Not testing technical configuration")
@Suppress("detekt.UtilityClassWithPublicConstructor")
internal class GitInfoConfiguration {
    companion object {
        @Bean
        @JvmStatic
        fun placeholderConfigurer(): PropertySourcesPlaceholderConfigurer {
            val propsConfig = PropertySourcesPlaceholderConfigurer()
            propsConfig.setLocation(ClassPathResource("git.properties"))
            propsConfig.setIgnoreResourceNotFound(true)
            propsConfig.setIgnoreUnresolvablePlaceholders(true)
            return propsConfig
        }
    }
}
