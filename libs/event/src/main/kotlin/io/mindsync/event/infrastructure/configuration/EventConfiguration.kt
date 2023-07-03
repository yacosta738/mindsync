package io.mindsync.event.infrastructure.configuration

import io.mindsync.event.domain.*
import io.mindsync.event.infrastructure.EventEmitter
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration

@Suppress("UNCHECKED_CAST")
@Configuration
class EventConfiguration(
    private val applicationContext: ApplicationContext
) {
    @Autowired(required = true)
    fun configEventEmitter(eventEmitter: EventEmitter<DomainEvent>) {
        applicationContext.getBeansOfType(EventConsumer::class.java).values.forEach {
            it.javaClass.annotations.filterIsInstance<Subscribe>()
                .forEach { annotation ->
                    val filter = createFilter<DomainEvent>(annotation)
                    eventEmitter.on(filter, it)
                }
        }
    }

    private inline fun <reified T : DomainEvent> createFilter(mapping: Subscribe): EventFilter<T> {
        val filterBeen = try {
            applicationContext.getBean(mapping.filterBy.java)
        } catch (_: BeansException) {
            TypeMatchEventFilter(mapping.filterBy)
        }

        return if (filterBeen is EventFilter<*>) {
            filterBeen as EventFilter<T>
        } else {
            TypeMatchEventFilter(mapping.filterBy) as EventFilter<T>
        }
    }
}
