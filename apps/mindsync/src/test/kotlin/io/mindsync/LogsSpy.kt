package io.mindsync

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.extension.*
import org.slf4j.LoggerFactory
import java.util.function.Predicate


private const val PACKAGE_NAME = "io.mindsync"

class LogsSpy : BeforeEachCallback, AfterEachCallback,
  ParameterResolver {
  private lateinit var logger: Logger
  private lateinit var appender: ListAppender<ILoggingEvent>
  private lateinit var initialLevel: Level
  override fun beforeEach(context: ExtensionContext) {
    appender = ListAppender()
    logger = LoggerFactory.getLogger(PACKAGE_NAME) as Logger
    logger.addAppender(appender)
    initialLevel = logger.level
    logger.level = Level.TRACE
    appender.start()
  }

  override fun afterEach(context: ExtensionContext) {
    logger.level = initialLevel
    logger.detachAppender(appender)
  }

  fun shouldHave(level: Level, content: String): LogsSpy {
    Assertions.assertThat(appender.list).anyMatch(withLog(level, content))
    return this
  }

  fun shouldHave(level: Level, content: String, count: Int): LogsSpy {
    Assertions.assertThat(appender.list.stream().filter(withLog(level, content))).hasSize(count)
    return this
  }

  fun shouldNotHave(level: Level, content: String): LogsSpy {
    Assertions.assertThat(appender.list).noneMatch(withLog(level, content))
    return this
  }

  private fun withLog(level: Level, content: String): Predicate<ILoggingEvent> {
    return Predicate { event: ILoggingEvent ->
      level == event.level && event.toString().contains(content)
    }
  }

  @Throws(ParameterResolutionException::class)
  override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
    return parameterContext.parameter.type == LogsSpy::class.java
  }

  @Throws(ParameterResolutionException::class)
  override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): LogsSpy {
    return this
  }
}

