package com.vacationtracker.employee.services

import com.vacationtracker.employee.models.Vacation
import com.vacationtracker.employee.repositories.EmployeeRepository
import com.vacationtracker.employee.repositories.VacationRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val vacationRepository: VacationRepository
) {
    fun getVacationDaysPerYear(employeeEmail: String, year: Int): List<Int?> {
        val employee = employeeRepository.findByEmail(employeeEmail).get()

        val totalDays = employee.totalVacationDays[year]
        val usedDays = vacationRepository.countAllByEmployee(employee)
        val availableDays = totalDays?.minus(usedDays)

        return listOf(totalDays, usedDays, availableDays)
    }

    fun getUsedVacationDaysForTimePeriod(employeeEmail: String, startDateString: String, endDateString: String): List<Vacation> {
        val employee = employeeRepository.findByEmail(employeeEmail).get()
        val startDate = LocalDate.parse(startDateString)
        val endDate = LocalDate.parse(endDateString)

        return vacationRepository.findRecordsForTimePeriod(employee, startDate, endDate)
    }

    fun insertNewRecord(employeeEmail: String, startDateString: String, endDateString: String): Vacation {
        val employee = employeeRepository.findByEmail(employeeEmail).get()
        val startDate = LocalDate.parse(startDateString)
        val endDate = LocalDate.parse(endDateString)

        val vacation = Vacation(startDate = startDate, endDate = endDate, employee = employee)

        return vacationRepository.save(vacation)
    }
}