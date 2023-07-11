package io.mindsync.healthcheck

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Controller class for performing health checks.
 * This class handles the '/health-check' endpoint.
 * The class contains a method to perform a health check, which returns a Mono of String.
 * The method returns a Mono of String with the value 'OK'.
 * @author Yuniel Acosta
 * @created 8/7/23
 */
@RestController
@RequestMapping("/health-check")
class HealthcheckController {

    /**
     * Checks the health status of the application.
     *
     * @return a Mono emitting a string "OK" indicating that the application is healthy.
     */
    @GetMapping
    fun healthcheck(): Mono<String> = Mono.just("OK")
}
