package io.mindsync.healthcheck

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/health-check")
class HealthcheckController {

    @GetMapping
    fun healthcheck(): Mono<String> = Mono.just("OK")
}
