package io.mindsync.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

/**
 * This class is responsible for configuring web-related settings for the application.
 * @author Yuniel Acosta
 * @since 0.0.0-SNAPSHOT
 * @see WebFluxConfigurer
 */
@Configuration
@EnableWebFlux
class WebConfig : WebFluxConfigurer {

    /**
     * Adds resource handlers to the given registry.
     *
     * @param registry the resource handler registry to add the resource handlers to
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
    }

    /**
     * Creates a RouterFunction for handling the root index ("/") request.
     *
     * @param html The static HTML resource to be returned as the response.
     *             Must not be null.
     *
     * @return A RouterFunction that handles the root index ("/") request
     *         and returns the specified HTML resource as the response.
     */
    @Bean
    fun indexRouter(@Value("classpath:/static/index.html") html: Resource): RouterFunction<ServerResponse> {
        return router {
            GET("/") {
                ok().contentType(MediaType.TEXT_HTML).bodyValue(html)
            }
        }
    }
}
