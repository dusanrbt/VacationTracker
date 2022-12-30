package com.vacationtracker.database.service.implementation

import com.vacationtracker.database.exception.EmployeeNotFound
import com.vacationtracker.database.exception.NoYearInVacationDays
import com.vacationtracker.database.model.Employee
import com.vacationtracker.database.repository.EmployeeRepository
import com.vacationtracker.database.service.interfaces.IEmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EmployeeService : IEmployeeService {

    companion object Constants {
        private const val EMPLOYEE_NOT_FOUND: String = "Employee not found."
        private const val NO_YEAR_IN_AVAILABLE_VACATIONS: String = "Year does not exist in available vacations list: "
    }

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

    override fun deleteAll() {
        employeeRepository.deleteAll()
    }

    override fun findByEmail(employeeEmail: String): Employee {
        val employeeOptional = employeeRepository.findByEmail(employeeEmail)
        if (employeeOptional.isEmpty) {
            throw EmployeeNotFound(EMPLOYEE_NOT_FOUND)
        }

        return employeeOptional.get()
    }

    override fun getVacationDaysPerYear(employee: Employee, year: Int): Int {
        if (!employee.totalVacationDays.containsKey(year)) {
            throw NoYearInVacationDays(NO_YEAR_IN_AVAILABLE_VACATIONS + year)
        }

        return employee.totalVacationDays.getValue(year)
    }

    override fun findById(employeeId: Long): Employee {
        val employeeOptional = employeeRepository.findById(employeeId)
        if (employeeOptional.isEmpty) {
            throw EmployeeNotFound(EMPLOYEE_NOT_FOUND)
        }

        return employeeOptional.get()
    }
}