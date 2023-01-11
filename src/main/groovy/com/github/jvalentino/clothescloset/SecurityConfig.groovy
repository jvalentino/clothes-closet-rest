package com.github.jvalentino.clothescloset

import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

/**
 * Stop requiring the spring boot login screen on every endpoint
 * @author john.valentino
 */
@CompileDynamic
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SuppressWarnings(['DuplicateStringLiteral'])
class SecurityConfig {

    static final List<String> INSECURES = [
            '/oauth',
            '/appointment/settings',
            '/appointment/schedule',
            '/settings',
    ]

    @Autowired
    CustomAuthenticationProvider authProvider

    @Autowired
    SecurityFilter securityFilter

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        (web) -> web.ignoring().antMatchers(
                '/oauth',
                '/appointment/settings',
                '/appointment/schedule',
                '/settings',
        )
    }

    @Bean
    AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder)
        authenticationManagerBuilder.authenticationProvider(authProvider)
        authenticationManagerBuilder.build()
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // POST with security was given 403

        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers('/')
                .hasRole('ADMIN')
                .anyRequest()
                .authenticated()

        http.addFilterAfter(securityFilter, BasicAuthenticationFilter)
        http.build()
    }

}
