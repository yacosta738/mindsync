package io.mindsync.user.domain

import io.mindsync.common.domain.BaseValidateValueObject
import io.mindsync.common.domain.BaseValueObject

data class Password(val password: String) :
    BaseValueObject<String>(password) {
    override fun compareTo(other: BaseValueObject<String>): Int {
        return this.value.compareTo(other.value)
    }

}