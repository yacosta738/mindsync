package io.mindsync

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.io.IOException


object JsonHelper {
  private val jsonMapper = jsonMapper()
  fun jsonMapper(): ObjectMapper {
    return JsonMapper
      .builder()
      .serializationInclusion(JsonInclude.Include.NON_NULL)
      .addModule(JavaTimeModule())
      .addModules(Jdk8Module())
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .build()
  }

  fun <T> writeAsString(`object`: T): String {
    return try {
      jsonMapper.writeValueAsString(`object`)
    } catch (e: JsonProcessingException) {
      throw AssertionError("Error serializing object: " + e.message, e)
    }
  }

  fun <T> readFromJson(json: String?, clazz: Class<T>?): T {
    return try {
      jsonMapper.readValue(json, clazz)
    } catch (e: IOException) {
      throw AssertionError("Error reading value from json: " + e.message, e)
    }
  }
}
