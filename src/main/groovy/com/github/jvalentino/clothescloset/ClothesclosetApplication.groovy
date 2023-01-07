package com.github.jvalentino.clothescloset

import groovy.transform.CompileDynamic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * The main application entrypoint
 * @author john.valentino
 */
@SpringBootApplication
@CompileDynamic
class ClothesclosetApplication {

    static void main(String[] args) {
        SpringApplication.run(ClothesclosetApplication, args)
    }

}
