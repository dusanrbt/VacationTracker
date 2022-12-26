package com.vacationtracker.database.service.implementation

import com.vacationtracker.database.exception.EmployeeNotFoundException
import com.vacationtracker.database.exception.NoYearInVacationDaysException
import com.vacationtracker.database.model.Employee
import com.vacationtracker.database.repository.EmployeeRepository
import com.vacationtracker.database.service.interfaces.IEmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EmployeeService : IEmployeeService {

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    override fun saveAll(employeeList: List<Employee>): Int {
        val employeesAdded = employeeRepository.saveAll(employeeList)

        return employeesAdded.size
    }

    override fun save(employee: Employee): Long {
        val employee = employeeRepository.save(employee)

        return employee.id
    }

    override fun findByEmail(employeeEmail: String): Employee {
        val employee = employeeRepository.findByEmail(employeeEmail)
        if (employee.isEmpty) {
            throw EmployeeNotFoundException("Employee not found.")
        }

        return employee.get()
    }

    override fun getVacationDaysPerYear(employeeEmail: String, year: Int): Int {
        val employeeOptional = employeeRepository.findByEmail(employeeEmail)
        if (employeeOptional.isEmpty) {
            throw EmployeeNotFoundException("Employee not found.")
        }

        val employee = employeeOptional.get()

        if (!employee.totalVacationDays.containsKey(year)) {
            throw NoYearInVacationDaysException("Employee doesn't have year $year in totalVacationDays.")
        }

        return employee.totalVacationDays.getValue(year)
    }

    override fun findById(employeeId: Long): Employee {
        val employee = employeeRepository.findById(employeeId)
        if (employee.isEmpty) {
            throw EmployeeNotFoundException("Employee not found.")
        }

        return employee.get()
    }
}