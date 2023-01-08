package com.github.jvalentino.clothescloset

import groovy.transform.CompileDynamic
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
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

    @Override
    void addCorsMappings(CorsRegistry registry) {
        registry.addMapping('/**')
    }

}
