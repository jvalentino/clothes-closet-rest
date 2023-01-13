package com.github.jvalentino.clothescloset

import com.github.jvalentino.clothescloset.config.DataSourceConfig
import spock.lang.Specification
import spock.lang.Subject

class DataSourceConfigTest extends Specification {

    @Subject
    DataSourceConfig subject

    def setup() {
        subject = new DataSourceConfig()
    }

    def "test extractConnectionInfo"() {
        given:
        String url = 'postgres://USERNAME:PASSWORD@HOST:5432/DBNAME'

        when:
        Map result = subject.extractConnectionInfo(url)

        then:
        result.username == 'USERNAME'
        result.password == 'PASSWORD'
        result.jdbc == 'jdbc:postgresql://HOST:5432/DBNAME'
    }

}
