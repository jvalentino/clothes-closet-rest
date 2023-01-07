package com.github.jvalentino.clothescloset

import groovy.transform.CompileDynamic
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.sql.DataSource

/**
 * I have to pull the busted DATABASE_URL out of the heroku environment to turn
 * it into something that works
 * @author john.valentino
 */
@CompileDynamic
@Configuration
class DataSourceConfig {

    // postgres://USERNAME:PASSWORD@HOST:5432/DBNAME
    // jdbc:postgresql://HOST:5432/DBNAME
    Map extractConnectionInfo(String url) {
        String[] split = url.split('[/@:]')
        Map map = [
                'username':split[3],
                'password':split[4],
                'jdbc':"jdbc:postgresql://${split[5]}:${split[6]}/${split[7]}",
        ]
        map
    }

    @Bean
    @SuppressWarnings(['DuplicateStringLiteral'])
    DataSource getDataSource() {
        String url = System.getenv('DATABASE_URL')

        String username = 'postgres'
        String password = 'postgres'
        String jdbc = 'jdbc:postgresql://localhost:5432/ccdb'

        if (url != null) {
            Map result = this.extractConnectionInfo(url)
            username = result.username
            password = result.password
            jdbc = result.jdbc
        }

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create()
        dataSourceBuilder.driverClassName('org.postgresql.Driver')
        dataSourceBuilder.url(jdbc)
        dataSourceBuilder.username(username)
        dataSourceBuilder.password(password)

        dataSourceBuilder.build()
    }

}
