package com.github.jvalentino.clothescloset.config

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileDynamic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * CORS
 * @author john.valentino
 */
@CompileDynamic
@Configuration
@EnableWebMvc
class WebConfig implements WebMvcConfigurer {

    /*@Override
    void addCorsMappings(CorsRegistry registry) {
        registry.addMapping('/**')
                .allowCredentials(true)
                .allowedHeaders('x-auth-token', 'Content-Type')
                .allowedMethods('*')
                .allowedOriginPatterns('*')
                .allowedOrigins('*')
    }*/

    @Bean(name = 'jsonMapper')
    @Primary
    ObjectMapper jsonMapper() {
        new CustomObjectMapper()
    }

    @Override
    void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(jsonMapper()))
    }

}
