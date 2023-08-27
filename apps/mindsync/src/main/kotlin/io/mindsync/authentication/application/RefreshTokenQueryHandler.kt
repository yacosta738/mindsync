package io.mindsync.authentication.application

import io.mindsync.authentication.application.query.RefreshTokenQuery
import io.mindsync.authentication.domain.AccessToken
import io.mindsync.authentication.domain.RefreshToken
import io.mindsync.authentication.domain.RefreshTokenManager
import io.mindsync.common.domain.Service
import io.mindsync.common.domain.query.QueryHandler
import org.slf4j.LoggerFactory

/**
 * Class for handling a RefreshTokenQuery and returning an AccessToken.
 *
 * @property refreshTokenManager The manager for refreshing tokens.
 */
@Service
class RefreshTokenQueryHandler(private val refreshTokenManager: RefreshTokenManager) :
    QueryHandler<RefreshTokenQuery, AccessToken> {

    /**
     * Handles the given query.
     * @param query The query to handle.
     * @return The response of the query.
     */
    override suspend fun handle(query: RefreshTokenQuery): AccessToken {
        log.debug("Handling query: {}", query)
        return refreshTokenManager.refresh(RefreshToken(query.refreshToken))
    }
    companion object {
        private val log = LoggerFactory.getLogger(RefreshTokenQueryHandler::class.java)
    }
}
