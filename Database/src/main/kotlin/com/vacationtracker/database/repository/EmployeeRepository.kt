package com.vacationtracker.database.repository

import com.vacationtracker.database.model.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface EmployeeRepository : JpaRepository<Employee, Long> {
    fun findByEmail(email: String): Optional<Employee>
}