//package io.mindsync.unit.user.infrastructure.rest
//
//import io.mindsync.user.infrastructure.persistence.entity.UserEntity
//import io.mindsync.user.infrastructure.persistence.repository.admin.AdminUserRepository
//import io.mindsync.user.infrastructure.persistence.repository.standard.StandardUserRepository
//import io.mindsync.user.infrastructure.rest.UserController
//import io.mockk.every
//import io.mockk.mockk
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//import reactor.test.StepVerifier
//
//private const val userId = "0"
//
//internal class UserEntityControllerTest {
//
//    companion object {
//        lateinit var uut: UserController
//    }
//
//    @BeforeEach
//    internal fun setUp() {
//        uut = UserController(mockAdminUserRepository(), mockStandardRepository())
//    }
//
//    @Test
//    internal fun shouldDelete() {
//        val deleteMono = uut.delete(userId)
//
//        StepVerifier.create(deleteMono)
//            .verifyComplete()
//    }
//
//    @Test
//    internal fun shouldFindAll() {
//        val usersMono = uut.findAll()
//
//        StepVerifier.create(usersMono)
//            .assertNext { user ->
//                assertEquals(userId, user.id)
//                assertEquals("test", user.name)
//            }
//            .verifyComplete()
//    }
//
//    private fun mockAdminUserRepository(): AdminUserRepository {
//        val repository = mockk<AdminUserRepository>()
//
//        every { repository.deleteById(ofType(String::class)) } returns Mono.just("").then()
//        every { repository.findAll() } returns Flux.just(UserEntity(userId, "test"))
//
//        return repository
//    }
//
//    private fun mockStandardRepository(): StandardUserRepository {
//        val repository = mockk<StandardUserRepository>()
//
//        // TODO finish mock
//
//        return repository
//    }
//}
