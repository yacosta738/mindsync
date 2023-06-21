//package io.mindsync.unit.user.infrastructure.rest.register
//
//import io.mindsync.user.infrastructure.rest.register.RegisterUserRequest
//import io.mindsync.user.infrastructure.rest.register.RegistrationDetails
//import io.mindsync.user.infrastructure.rest.register.UserRegistryController
//import io.mindsync.user.infrastructure.service.RegisterService
//import io.mockk.every
//import io.mockk.mockk
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import reactor.core.publisher.Mono
//import reactor.test.StepVerifier
//import java.util.*
//
//internal class UserRegistryControllerTest {
//
//    companion object {
//        lateinit var uut: UserRegistryController
//    }
//
//    @BeforeEach
//    internal fun setUp() {
//        uut = UserRegistryController(mockUserRegistrationWorkflow())
//    }
//
//    @Test
//    internal fun shouldBeginRegistration() {
//        val uuidMono = uut.initRegistration(
//            RegisterUserRequest("firstname", "lastname", "test@email.com")
//        )
//
//        StepVerifier.create(uuidMono)
//            .assertNext { uuid -> assertEquals("123-456-789", uuid) }
//            .verifyComplete()
//    }
//
//    @Test
//    internal fun shouldCompleteRegistration() {
//        val monoVoid = uut.completeRegistration(UUID.randomUUID())
//
//        StepVerifier.create(monoVoid)
//            .verifyComplete()
//    }
//
//    private fun mockUserRegistrationWorkflow(): RegisterService {
//        val workflow = mockk<RegisterService>()
//
//        every { workflow.beginRegistration(ofType(RegistrationDetails::class)) } returns Mono.just("123-456-789")
//        every { workflow.confirmRegistration(ofType(UUID::class)) } returns Mono.empty()
//
//        return workflow
//    }
//}