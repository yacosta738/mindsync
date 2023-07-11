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

/**
 * Configuration class for SpringDoc.
 *
 * This class is responsible for configuring the SpringDoc library
 * and provides beans for generating OpenAPI documentation.
 * @see OpenAPI for more information about the OpenAPI specification.
 * @see GroupedOpenApi for more information about the SpringDoc library.
 * @see Bean for more information about the annotation.
 * @see Configuration for more information about the annotation.
 * @see Value for more information about the annotation.
 * @see Generated for more information about the annotation.
 * @see SpringdocConfiguration for more information about the configuration class.
 *
 * @author Yuniel Acosta
 */
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
