//package io.mindsync.integration.infrastructure.rest.register.mapper
//
//import io.mindsync.user.domain.*
//import io.mindsync.user.infrastructure.mapper.UserMapper
//import io.mindsync.user.infrastructure.persistence.entity.UserEntity
//import io.mindsync.user.infrastructure.rest.register.RegisterUserRequest
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.Test
//import java.time.LocalDateTime
//import java.util.*
//
//private const val EMAIL = "yap@gmail.com"
//
//private const val ROLE_USER = "ROLE_USER"
//
//private const val FIRSTNAME = "Yap"
//
//private const val PASSWORD = "password"
//
//internal class UserMapperTest {
//    private var userMapper: UserMapper = UserMapper()
//
//    @Test
//    fun `should map to user register command`() {
//        val registerUserRequest = RegisterUserRequest("firstname", "lastname", "email", PASSWORD)
//
//        val userRegisterCommand = userMapper.toUserRegisterCommand(registerUserRequest)
//
//        assertThat(userRegisterCommand.firstName).isEqualTo("firstname")
//        assertThat(userRegisterCommand.lastName).isEqualTo("lastname")
//        assertThat(userRegisterCommand.email).isEqualTo("email")
//        assertThat(userRegisterCommand.password).isEqualTo(PASSWORD)
//    }
//
//    @Test
//    fun `should map to user register command with empty values`() {
//        val registerUserRequest = RegisterUserRequest()
//
//        val userRegisterCommand = userMapper.toUserRegisterCommand(registerUserRequest)
//
//        assertThat(userRegisterCommand.firstName).isEqualTo("")
//        assertThat(userRegisterCommand.lastName).isEqualTo("")
//        assertThat(userRegisterCommand.email).isEqualTo("")
//        assertThat(userRegisterCommand.password).isEqualTo("")
//    }
//
//    @Test
//    fun `should map user to user entity`() {
//        val user = User(
//            id = UserId(UUID.randomUUID()),
//            email = Email(EMAIL),
//            name = Name(FIRSTNAME, FIRSTNAME),
//            password = Password(PASSWORD),
//            authorities = setOf(ROLE_USER),
//            locale = Locale("en"),
//            createdBy = UserId(UUID.randomUUID()),
//            createdDate = LocalDateTime.now(),
//            lastModifiedBy = UserId(UUID.randomUUID()),
//            lastModifiedDate = LocalDateTime.now()
//        )
//
//        val userEntity = userMapper.toUserEntity(user)
//
//        assertThat(userEntity.id).isEqualTo(user.id.value.toString())
//        assertThat(userEntity.email).isEqualTo(user.getEmail().email)
//        assertThat(userEntity.firstName).isEqualTo(user.getName().firstName.value)
//        assertThat(userEntity.lastName).isEqualTo(user.getName().lastName.value)
//        assertThat(userEntity.password).isEqualTo(user.getPassword().password)
//        userEntity.authorities.forEach { assertThat(user.getAuthorities()).contains(it.name) }
//        assertThat(userEntity.locale).isEqualTo(user.getLocale())
//        assertThat(userEntity.status).isEqualTo(user.getStatus().name)
//        assertThat(userEntity.createdBy?.id ?: "").isEqualTo(user.createdBy.value.toString())
//        assertThat(userEntity.createdDate).isEqualTo(user.createdDate)
//        assertThat(userEntity.lastModifiedBy?.id ?: "").isEqualTo(user.lastModifiedBy.value.toString())
//        assertThat(userEntity.lastModifiedDate).isEqualTo(user.lastModifiedDate)
//    }
//
//    @Test
//    fun `should map user entity to user`() {
//        val userEntity = UserEntity(
//            id = UUID.randomUUID().toString(),
//            email = EMAIL,
//            firstName = FIRSTNAME,
//            lastName = FIRSTNAME,
//            password = PASSWORD,
//            authorities = setOf(),
//            locale = Locale.ENGLISH,
//            status = UserStatus.ACTIVE.name,
//            createdBy = UserEntity(UUID.randomUUID().toString()),
//            createdDate = LocalDateTime.now(),
//            lastModifiedBy = UserEntity(UUID.randomUUID().toString()),
//            lastModifiedDate = LocalDateTime.now()
//        )
//
//        val user = userMapper.toUser(userEntity)
//
//        assertThat(user.id.value.toString()).isEqualTo(userEntity.id)
//        assertThat(user.getEmail().email).isEqualTo(userEntity.email)
//        assertThat(user.getName().firstName.value).isEqualTo(userEntity.firstName)
//        assertThat(user.getName().lastName.value).isEqualTo(userEntity.lastName)
//        assertThat(user.getPassword().password).isEqualTo(userEntity.password)
//        userEntity.authorities.forEach { assertThat(user.getAuthorities()).contains(it.name) }
//        assertThat(user.getLocale()).isEqualTo(userEntity.locale)
//        assertThat(user.getStatus().name).isEqualTo(userEntity.status)
//        assertThat(user.createdBy.value.toString()).isEqualTo(userEntity.createdBy?.id ?: "")
//        assertThat(user.createdDate).isEqualTo(userEntity.createdDate)
//        assertThat(user.lastModifiedBy.value.toString()).isEqualTo(userEntity.lastModifiedBy?.id ?: "")
//        assertThat(user.lastModifiedDate).isEqualTo(userEntity.lastModifiedDate)
//    }
//}
