package com.vacationtracker.database.service.interfaces

import com.vacationtracker.database.model.Employee
import org.springframework.stereotype.Service

@Service
interface IEmployeeService {
    fun saveAll(employees: List<Employee>): Int

    fun save(employee: Employee): Long

    fun deleteAll()

    fun findByEmail(employeeEmail: String): Employee

    fun getVacationDaysPerYear(employee: Employee, year: Int): Int

    fun findById(employeeId: Long): Employee
}