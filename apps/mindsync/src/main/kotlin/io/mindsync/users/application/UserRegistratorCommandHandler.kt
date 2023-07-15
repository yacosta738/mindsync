package io.mindsync.users.application

import io.mindsync.common.domain.Service
import io.mindsync.common.domain.command.CommandHandler
import io.mindsync.users.application.command.RegisterUserCommand
import org.slf4j.LoggerFactory

/**
 * Handles the command for registering a new user.
 *
 * @author Yuniel Acosta (acosta)
 * @created 29/6/23
 * @param userRegistrator the UserRegistrator service to use for registering the user
 */
@Service
class UserRegistratorCommandHandler(private val userRegistrator: UserRegistrator) :
    CommandHandler<RegisterUserCommand> {
    /**
     * Handles the given RegisterUserCommand.
     *
     * @param command The RegisterUserCommand to handle. See [RegisterUserCommand] for more information
     * about the command.
     */
    override suspend fun handle(command: RegisterUserCommand) {
        log.info("Handling register user command with email: {}", command.email)
        userRegistrator.registerNewUser(command)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserRegistratorCommandHandler::class.java)
    }
}
