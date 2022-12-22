package com.vacationtracker.admin.config

import com.vacationtracker.admin.models.Employee
import com.vacationtracker.admin.repositories.EmployeeRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdminConfig {

    /*@Bean
    fun commandLineRunner(repository: EmployeeRepository): CommandLineRunner {
        return CommandLineRunner { _ ->
            *//*val pera = Employee("pera@rbt.rs", "pera")
            val mika = Employee("mika@rbt.rs", "mika")
            val zika = Employee("zika@rbt.rs", "zika")

            repository.saveAll(listOf(pera, mika, zika))*//*
        }
    }*/
}