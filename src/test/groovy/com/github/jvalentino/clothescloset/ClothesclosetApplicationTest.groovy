package com.github.jvalentino.clothescloset

import org.springframework.boot.SpringApplication
import spock.lang.Specification

class ClothesclosetApplicationTest extends Specification {

    def setup() {
    }

    def "test main"() {
        given:
        GroovyMock(SpringApplication, global:true)

        when:
        ClothesclosetApplication.main(null)

        then:
        1 * SpringApplication.run(ClothesclosetApplication, null)
    }

}
