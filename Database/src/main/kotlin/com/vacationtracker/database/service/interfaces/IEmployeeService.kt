package com.vacationtracker.database.service.interfaces

import com.vacationtracker.database.model.Employee
import org.springframework.stereotype.Service

@Service
interface IEmployeeService {
    fun saveAll(employees: List<Employee>): Int

    fun save(employee: Employee): Long

    fun findByEmail(employeeEmail: String): Employee

    fun getVacationDaysPerYear(employeeEmail: String, year: Int): Int

    fun findById(employeeId: Long): Employee
}