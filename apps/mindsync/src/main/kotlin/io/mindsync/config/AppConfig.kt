package io.mindsync.config


import io.mindsync.authentication.infrastructure.ApplicationSecurityProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ApplicationSecurityProperties::class)
class AppConfig
