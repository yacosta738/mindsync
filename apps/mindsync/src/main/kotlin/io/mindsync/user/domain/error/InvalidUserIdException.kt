package io.mindsync.user.domain.error

import io.mindsync.common.domain.error.InvalidArgumentException

class InvalidUserIdException(id: String, exception: Exception) : InvalidArgumentException(
    "The user id <$id> is not valid",
    exception
)
