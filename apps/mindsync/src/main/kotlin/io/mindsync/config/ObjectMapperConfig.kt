package io.mindsync.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class ObjectMapperConfig {

    @Bean
    @Primary
    fun objectMapper(): JsonMapper {
        val kotlinModule = KotlinModule.Builder()
            .enable(KotlinFeature.StrictNullChecks)
            .build()
        val mapper = JsonMapper.builder()
            .addModule(kotlinModule)
            .build()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        val javaTimeModule = JavaTimeModule()
        mapper.registerModule(javaTimeModule)

        return mapper
    }
}
