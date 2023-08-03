package io.mindsync.users.domain

/**
 * Value object that represents a first name
 * @created 2/7/23
 * @param firstName first name value object
 * @param lastName last name value object
 */
data class Name(val firstName: FirstName?, val lastName: LastName?) : Comparable<Name> {

    constructor(firstName: String, lastName: String) : this(
        FirstName(firstName),
        LastName(lastName)
    )

    /**
     * Returns the full name of the user (first name + last name)
     * @return the full name of the user
     */
    fun fullName(): String {
        return "$firstName $lastName"
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override operator fun compareTo(other: Name): Int {
        return fullName().compareTo(other.fullName())
    }
}
