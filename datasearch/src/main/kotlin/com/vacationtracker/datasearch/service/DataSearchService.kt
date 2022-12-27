package com.vacationtracker.datasearch.service

import com.vacationtracker.database.model.Vacation
import com.vacationtracker.database.service.implementation.EmployeeService
import com.vacationtracker.database.service.implementation.VacationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class DataSearchService {

    @Autowired
    private lateinit var employeeService: EmployeeService

    @Autowired
    private lateinit var vacationService: VacationService

    fun getVacationDaysPerYear(employeeEmail: String, year: Int): List<Int?> {
        val employee = employeeService.findByEmail(employeeEmail)
        val totalDays = employee.totalVacationDays[year]
        val usedDays = vacationService.countAllByEmployee(employee)
        val availableDays = totalDays?.minus(usedDays)

        return listOf(totalDays, usedDays, availableDays)
    }

    fun getUsedVacationDaysForTimePeriod(employeeEmail: String, startDateString: String, endDateString: String): List<Vacation> {
        val employee = employeeService.findByEmail(employeeEmail)
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val startDate = formatter.parse(startDateString)
        val endDate = formatter.parse(endDateString)

        return vacationService.findRecordsForTimePeriod(employee, startDate, endDate)
    }

    fun insertNewRecord(employeeEmail: String, startDateString: String, endDateString: String): Long {
        val employee = employeeService.findByEmail(employeeEmail)
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val startDate = formatter.parse(startDateString)
        val endDate = formatter.parse(endDateString)

        val vacation = Vacation(startDate = startDate, endDate = endDate, employee = employee)

        return vacationService.save(vacation)
    }
}