package io.mindsync.common.domain.command

import java.util.*

/**
 * Represents a command in the system.
 * This interface defines the properties and behavior that all commands in the system must have.
 *
 * @property id The unique identifier of the command.
 * @author Yuniel Acosta
 * @created 29/6/23
 */
interface Command {
    val id: UUID
}
