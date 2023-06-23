package io.mindsync

import io.mindsync.common.domain.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories

@SpringBootApplication
@ComponentScan(includeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [Service::class])])
@EnableReactiveNeo4jRepositories(basePackages = ["io.mindsync"])
class MindsyncApp

private val log: Logger = LoggerFactory.getLogger(MindsyncApp::class.java)

fun main(args: Array<String>) {
    val environment = runApplication<MindsyncApp>(args = args).environment

    if (log.isInfoEnabled) {
        log.info(ApplicationStartupTraces.of(environment))
    }
}
