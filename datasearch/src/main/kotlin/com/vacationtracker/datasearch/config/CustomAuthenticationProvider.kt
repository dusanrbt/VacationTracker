package com.vacationtracker.datasearch.config

import com.vacationtracker.database.service.implementation.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component


@Component
class CustomAuthenticationProvider : AuthenticationProvider {

    @Autowired
    private lateinit var employeeService: EmployeeService

    override fun authenticate(authentication: Authentication): Authentication? {
        val email = authentication.name
        val password = authentication.credentials.toString()

        val employee = employeeService.findByEmail(email)
        return if (employee.password == password) {
            UsernamePasswordAuthenticationToken(email, password, ArrayList())
        } else {
            null
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}