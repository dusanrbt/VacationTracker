package com.vacationtracker.datasearch.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig {

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder): InMemoryUserDetailsManager {
        authenticationManagerBuilder
        val user = User.builder().username("user1@rbt.rs").password(encoder().encode("123")).roles("USER").build()
        return InMemoryUserDetailsManager(user)
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