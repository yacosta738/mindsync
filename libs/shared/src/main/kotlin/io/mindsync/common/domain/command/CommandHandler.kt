package io.mindsync.common.domain.command

/**
 * The CommandHandler interface represents a handler for specific commands.
 *
 * @param T The type of command to be handled.
 * @author Yuniel Acosta
 * @created 29/6/23
 */

fun interface CommandHandler<T : Command> {
    /**
     * Handles the given command.
     *
     * @param command The command to be handled.
     */
    suspend fun handle(command: T)
}
