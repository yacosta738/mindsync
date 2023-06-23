package io.mindsync.config.springdoc

import io.mindsync.common.domain.Generated
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springdoc.core.GroupedOpenApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Generated(reason = "Not testing technical configuration")
internal class SpringdocConfiguration {
    @Value("\${application.version:undefined}")
    private val version: String? = null

    @Bean
    fun mindsyncOpenAPI(): OpenAPI {
        return OpenAPI().info(swaggerInfo()).externalDocs(swaggerExternalDoc())
    }

    private fun swaggerInfo(): Info {
        return Info().title("Project API").description("Project description API").version(version)
            .license(License().name("No license").url(""))
    }

    private fun swaggerExternalDoc(): ExternalDocumentation {
        return ExternalDocumentation().description("Project Documentation").url("")
    }

    @Bean
    fun mindsyncAllOpenAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder().group("all").pathsToMatch("/api/**").build()
    }
}
