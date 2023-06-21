package io.mindsync.healthcheck

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/profile")
class DummyController {
  @GetMapping
  fun profile():Mono<String> = Mono.just("OK")

  @GetMapping("/secure-admin")
  @PreAuthorize("hasRole('ADMIN')")
  fun secureAdmin():Mono<String> = Mono.just("You are admin")

}
