package io.mindsync.user.domain

import io.mindsync.common.domain.AggregateRoot
import java.util.*

class User(
    override val id: UserId,
    private var email: Email,
    private var name: Name,
    private var password: Password,
    private val authorities: Set<String> = LinkedHashSet(),
    private var locale: Locale = Locale.ENGLISH,
    private var status: UserStatus = UserStatus.ACTIVE,
) : AggregateRoot<UserId>(){

    fun changeEmail(email: Email) {
        this.email = email
    }

    fun changePassword(password: Password) {
        this.password = password
    }

    fun changeName(name: Name) {
        this.name = name
    }

    fun changeLocale(locale: Locale) {
        this.locale = locale
    }

    fun changeStatus(status: UserStatus) {
        this.status = status
    }

    fun addAuthority(authority: String) {
        authorities.plus(authority)
    }

    fun removeAuthority(authority: String) {
        authorities.minus(authority)
    }

    fun getEmail(): Email {
        return email
    }

    fun getName(): Name {
        return name
    }

    fun getPassword(): Password {
        return password
    }

    fun getLocale(): Locale {
        return locale
    }

    fun getStatus(): UserStatus {
        return status
    }

    fun getAuthorities(): Set<String> {
        return authorities
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}
