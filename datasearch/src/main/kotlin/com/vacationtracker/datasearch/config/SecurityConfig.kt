package com.vacationtracker.datasearch.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain


@EnableWebSecurity
@Configuration
class SecurityConfig {

    @Autowired
    private lateinit var authenticationProvider: CustomAuthenticationProvider

    @Bean
    @Throws(java.lang.Exception::class)
    fun configure(http: HttpSecurity): AuthenticationManager? {
        val authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java
        )
        authenticationManagerBuilder.authenticationProvider(authenticationProvider)
        return authenticationManagerBuilder.build()
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable().authorizeHttpRequests { auth ->
            auth.anyRequest().authenticated()
        }.httpBasic()

        return http.build()
    }

}