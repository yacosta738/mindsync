package io.mindsync.users.application

import io.mindsync.common.domain.Service
import io.mindsync.common.domain.command.CommandHandler
import io.mindsync.users.application.command.RegisterUserCommand
import org.slf4j.LoggerFactory

/**
 * UserRegistratorCommandHandler is a CommandHandler for RegisterUserCommand
 * @author acosta
 * @created 29/6/23
 */
@Service
class UserRegistratorCommandHandler(private val userRegistrator: UserRegistrator) :
    CommandHandler<RegisterUserCommand> {
    override suspend fun handle(command: RegisterUserCommand) {
        log.info("Handling register user command with email: {}", command.email)
        userRegistrator.registerNewUser(command)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserRegistratorCommandHandler::class.java)
    }
}
