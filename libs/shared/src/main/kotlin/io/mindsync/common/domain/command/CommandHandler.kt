package io.mindsync.common.domain.command

/**
 *
 * @author acosta
 * @created 29/6/23
 */

fun interface CommandHandler<T : Command> {
    suspend fun handle(command: T)
}
