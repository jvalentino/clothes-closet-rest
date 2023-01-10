package com.github.jvalentino.clothescloset

import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

/**
 * Stop requiring the spring boot login screen on every endpoint
 * @author john.valentino
 */
@CompileDynamic
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig /*extends WebSecurityConfigurerAdapter*/ {

    @Autowired
    CustomAuthenticationProvider authProvider


    @Autowired
    SecurityFilter securityFilter

    /*@Override
    protected void configure(HttpSecurity security) throws Exception {
        security.httpBasic().disable();
    }*/

    /*@Override
    void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                '/oauth',
                'appointment/settings',
                '/appointment/schedule'
        )
    }*/

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                '/oauth',
                'appointment/settings',
                '/appointment/schedule',
                '/settings'
        )
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        println 'SecurityFilterChain filterChain'

        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated()

        http.addFilterAfter(securityFilter, BasicAuthenticationFilter)
        return http.build();



    }



    /*@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        http.build()
    }*/

}
