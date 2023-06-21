package io.mindsync

import io.mindsync.common.domain.Generated
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream


object ApplicationStartupTraces {
    private val log: Logger = LoggerFactory.getLogger(ApplicationStartupTraces::class.java)
    private val SEPARATOR = "-".repeat(58)
    private const val BREAK = "\n"
    private const val SPACER = "  "

    fun of(environment: Environment): String{
        Objects.requireNonNull(environment, "Environment must not be null")
        return buildTraces(environment)
    }

    private fun buildTraces(environment: Environment): String {
        val trace = StringBuilder()
        trace.append(BREAK)
            .append(SEPARATOR).append(BREAK)
            .append(SPACER).append(applicationRunningTrace(environment)).append(BREAK)
            .append(SPACER).append(localUrl(environment)).append(BREAK)
            .append(SPACER).append(externalUrl(environment)).append(BREAK)
            .append(SPACER).append(profilesTrace(environment)).append(BREAK)
            .append(SEPARATOR).append(BREAK)
            .append(SPACER).append(configServer(environment)).append(BREAK)

        return trace.toString()
    }

    private fun applicationRunningTrace(environment: Environment): String {
        val applicationName = environment.getProperty("spring.application.name")
        return if (StringUtils.isBlank(applicationName)) {
            "Application is running!"
        } else StringBuilder().append("Application '").append(applicationName).append("' is running!")
            .toString()
    }
    private fun localUrl(environment: Environment): String {
        return url("Local", "localhost", environment)
    }

    private fun externalUrl(environment: Environment): String {
        return url("External", hostAddress(), environment)
    }

    private fun url(type: String, host: String, environment: Environment): String {
        return if (notWebEnvironment(environment)) {
            ""
        } else StringBuilder()
            .append(type)
            .append(": \t")
            .append(protocol(environment))
            .append("://")
            .append(host)
            .append(":")
            .append(port(environment))
            .append(contextPath(environment))
            .toString()
    }
    private fun notWebEnvironment(environment: Environment): Boolean {
        return StringUtils.isBlank(environment.getProperty("server.port"))
    }

    private fun protocol(environment: Environment): String {
        return if (noKeyStore(environment)) {
            "http"
        } else "https"
    }

    private fun noKeyStore(environment: Environment): Boolean {
        return StringUtils.isBlank(environment.getProperty("server.ssl.key-store"))
    }

    private fun port(environment: Environment): String? {
        return environment.getProperty("server.port")
    }

    private fun profilesTrace(environment: Environment): String {
        val profiles = environment.activeProfiles
        return if (profiles.isEmpty()) {
            "No active profile set, running with default configuration."
        } else java.lang.StringBuilder().append("Profile(s): \t")
            .append(Stream.of(*profiles).collect(Collectors.joining(", "))).toString()
    }

    @Generated(reason = "Hard to test implement detail error management")
    private fun hostAddress(): String {
        try {
            return InetAddress.getLocalHost().hostAddress
        } catch (e: UnknownHostException) {
            log.warn("The host name could not be determined, using `localhost` as fallback")
        }
        return "localhost"
    }

    private fun contextPath(environment: Environment): String? {
        val contextPath = environment.getProperty("server.servlet.context-path")
        return if (StringUtils.isBlank(contextPath)) {
            "/"
        } else contextPath
    }

    private fun configServer(environment: Environment): String {
        val configServer = environment.getProperty("configserver.status")
        return if (StringUtils.isBlank(configServer)) {
          "No config server detected."
        } else StringBuilder().append("Config Server: ").append(configServer).append(BREAK).append(SEPARATOR)
            .append(BREAK).toString()
    }

}
