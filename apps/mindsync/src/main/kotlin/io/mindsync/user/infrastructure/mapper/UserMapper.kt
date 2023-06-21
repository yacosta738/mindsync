//package io.mindsync.user.infrastructure.mapper
//
//import io.mindsync.user.application.UserRegisterCommand
//import io.mindsync.user.domain.*
//import io.mindsync.user.infrastructure.persistence.entity.AuthorityEntity
//import io.mindsync.user.infrastructure.persistence.entity.UserEntity
//import io.mindsync.user.infrastructure.rest.register.RegisterUserRequest
//import org.springframework.stereotype.Component
//import java.util.*
//
//@Component
//class UserMapper {
//    fun toUserRegisterCommand(registerUserRequest: RegisterUserRequest): UserRegisterCommand =
//        UserRegisterCommand(
//            email = registerUserRequest.email,
//            password = registerUserRequest.password,
//            firstName = registerUserRequest.firstName,
//            lastName = registerUserRequest.lastName
//        )
//
//    fun toUserEntity(user: User): UserEntity {
//        val name = user.getName()
//        return UserEntity(
//            id = user.id.value.toString(),
//            email = user.getEmail().email,
//            password = user.getPassword().password,
//            firstName = name.firstName.value,
//            lastName = name.lastName.value,
//            authorities = user.getAuthorities().map { AuthorityEntity(it) }.toSet(),
//            locale = user.getLocale()
//        )
//    }
//
//    fun toUser(userEntity: UserEntity): User {
//        return User(
//            id = UserId.fromString(userEntity.id?:""),
//            email = Email(userEntity.email?:""),
//            password = Password(userEntity.password?:""),
//            name = Name(userEntity.firstName?:"", userEntity.lastName?:""),
//            authorities = userEntity.authorities.map { it.name }.toSet(),
//            locale = userEntity.locale ?: Locale.ENGLISH
//        )
//    }
//}
