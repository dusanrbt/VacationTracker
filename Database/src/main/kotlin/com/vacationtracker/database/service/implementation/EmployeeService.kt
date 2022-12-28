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

    companion object Constants {
        private const val EMPLOYEE_NOT_FOUND_MSG: String = "Employee not found."
        private const val NO_YEAR_IN_AVAILABLE_VACATIONS: String = "Given year does not exist in employee available vacations list: "
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

    override fun findByEmail(employeeEmail: String): Employee {
        val employee = employeeRepository.findByEmail(employeeEmail)
        if (employee.isEmpty) {
            throw EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_MSG)
        }

        return employee.get()
    }

    override fun getVacationDaysPerYear(employeeEmail: String, year: Int): Int {
        val employeeOptional = employeeRepository.findByEmail(employeeEmail)
        if (employeeOptional.isEmpty) {
            throw EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_MSG)
        }

        val employee = employeeOptional.get()

        if (!employee.totalVacationDays.containsKey(year)) {
            throw NoYearInVacationDaysException(NO_YEAR_IN_AVAILABLE_VACATIONS + year)
        }

        return employee.totalVacationDays.getValue(year)
    }

    override fun findById(employeeId: Long): Employee {
        val employee = employeeRepository.findById(employeeId)
        if (employee.isEmpty) {
            throw EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_MSG)
        }

        return employee.get()
    }
}