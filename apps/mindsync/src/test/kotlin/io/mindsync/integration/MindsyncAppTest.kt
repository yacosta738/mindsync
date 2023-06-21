package io.mindsync.integration

import com.ninjasquad.springmockk.MockkBean
import io.mindsync.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository


@IntegrationTest
internal class MindsyncAppTest {
    @MockkBean
    private lateinit var clientRegistrations: ReactiveClientRegistrationRepository;

    @Test
    fun should_load_context() {
    }
}
