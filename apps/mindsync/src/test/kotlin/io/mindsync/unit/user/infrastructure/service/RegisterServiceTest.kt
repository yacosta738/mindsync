//package io.mindsync.unit.user.infrastructure.service
//
//import io.mindsync.unit.user.UserStub
//import io.mindsync.user.infrastructure.mapper.UserMapper
//import io.mindsync.user.infrastructure.persistence.entity.UserEntity
//import io.mindsync.user.infrastructure.persistence.repository.standard.StandardUserRepository
//import io.mindsync.user.infrastructure.security.KeycloakRepository
//import io.mindsync.user.infrastructure.security.KeycloakUser
//import io.mindsync.user.infrastructure.service.RegisterService
//import io.mockk.every
//import io.mockk.mockk
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import reactor.core.publisher.Mono
//import reactor.test.StepVerifier
//
//internal class RegisterServiceTest {
//
//    companion object {
//        lateinit var uut: RegisterService
//    }
//
//    @BeforeEach
//    internal fun setUp() {
//        uut = RegisterService(
//            mockStandardUserRepository(),
//            UserMapper(),
//            mockKeycloakRepository()
//        )
//    }
//
//    @Test
//    internal fun `should register a user`() {
//        val stubUser = UserStub.create()
//        val userRegistered = uut.register(stubUser)
//
//        StepVerifier.create(userRegistered)
//            .assertNext { user ->
//                assertEquals(stubUser.id, user.id)
//                assertEquals(stubUser.getName(), user.getName())
//                assertEquals(stubUser.getEmail(), user.getEmail())
//                assertEquals(stubUser.getPassword(), user.getPassword())
//                assertEquals(stubUser.getAuthorities(), user.getAuthorities())
//            }
//            .verifyComplete()
//    }
//
////    @Test
////    internal fun shouldCompleteRegistration() {
////        val voidMono = uut.confirmRegistration(UUID.randomUUID())
////
////        StepVerifier.create(voidMono)
////            .verifyComplete()
////    }
//
//    private fun mockStandardUserRepository(): StandardUserRepository {
//        val repository = mockk<StandardUserRepository>()
//
//        every { repository.save(ofType(UserEntity::class)) } returns Mono.just(UserEntity("123-456-789", "test"))
//
//        return repository
//    }
//
//    private fun mockKeycloakRepository(): KeycloakRepository {
//        val repository = mockk<KeycloakRepository>()
//
//        every { repository.createUser(ofType(KeycloakUser::class)) } returns Mono.just(KeycloakUser("test", "test", "test@email.com", "test"))
//
//        // TODO finish mock
//
//        return repository
//    }
//}
