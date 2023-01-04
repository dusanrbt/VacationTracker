package com.vacationtracker.dataupload.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        val filter = ApiKeyAuthenticationFilter("api-key")
        filter.setAuthenticationManager { authentication ->
            val principal = authentication.principal as String

            if (principal != "admin") {
                throw BadCredentialsException("The API key was not found or not the expected value.")
            }
            authentication.isAuthenticated = true
            authentication
        }

        http.csrf().disable().authorizeHttpRequests { auth ->
            auth.anyRequest().authenticated()
        }.addFilter(filter)

        return http.build()
    }

}