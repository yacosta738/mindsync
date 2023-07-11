package io.mindsync.config

import io.mindsync.authentication.infrastructure.ApplicationSecurityProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for the application.
 *
 * This class is responsible for configuring the application properties and enabling
 * the usage of configuration properties for application security.
 * @author Yuniel Acosta
 * @since 0.0.0-SNAPSHOT
 * @see EnableConfigurationProperties for more information about the annotation.
 * @see ApplicationSecurityProperties for more information about the configuration properties.
 */
@Configuration
@EnableConfigurationProperties(ApplicationSecurityProperties::class)
class AppConfig
