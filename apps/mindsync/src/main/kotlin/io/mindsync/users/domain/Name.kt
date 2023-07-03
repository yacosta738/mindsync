package io.mindsync.users.domain

data class Name(val firstName: FirstName?, val lastName: LastName?) : Comparable<Name> {

    constructor(firstName: String, lastName: String) : this(
        FirstName(firstName),
        LastName(lastName)
    )

    fun fullName(): String {
        return "$firstName $lastName"
    }

    override operator fun compareTo(other: Name): Int {
        return fullName().compareTo(other.fullName())
    }
}
