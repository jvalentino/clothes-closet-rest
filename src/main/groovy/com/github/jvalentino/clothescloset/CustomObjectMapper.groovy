package com.github.jvalentino.clothescloset

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import groovy.transform.CompileDynamic

// https://stackoverflow.com/questions/21708339/avoid-jackson-serialization-on-non-fetched-lazy-objects
// https://eresh-gorantla.medium.com/override-default-objectmapper-in-spring-boot-1c38c245ec0e
/**
 * I forgot I had to implement serialization magic to keep jackson from LAZY fetching
 * every single connected entity.
 * @author john.valentino
 */
@CompileDynamic
@SuppressWarnings(['UnnecessarySetter'])
class CustomObjectMapper extends ObjectMapper {

    CustomObjectMapper() {
        super()
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        this.registerModule(new Hibernate5Module())
    }

}
