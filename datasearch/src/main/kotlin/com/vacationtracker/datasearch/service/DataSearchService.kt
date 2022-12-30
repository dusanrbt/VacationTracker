package com.vacationtracker.datasearch.service

import com.vacationtracker.database.model.Vacation
import com.vacationtracker.database.service.implementation.EmployeeService
import com.vacationtracker.database.service.implementation.VacationService
import com.vacationtracker.datasearch.exception.InvalidDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Service
class DataSearchService {

    companion object Constants {
        private const val INVALID_DATE_FORMAT = "Invalid date format."
        private const val INVALID_DATE_ORDER = "Start date must be before or same as end date."
        private const val INVALID_DATE_TIME = "Dates should be before or equal as current date."
        private const val DATE_FORMAT = "yyyy-MM-dd"
    }

    @Autowired
    private lateinit var employeeService: EmployeeService

    @Autowired
    private lateinit var vacationService: VacationService

    fun getVacationDaysPerYear(employeeEmail: String, year: Int): List<Int> {
        val employee = employeeService.findByEmail(employeeEmail)
        val totalDays = employeeService.getVacationDaysPerYear(employee, year)
        val usedDays = vacationService.countAllByEmployee(employee)
        val availableDays = totalDays.minus(usedDays)

        return listOf(totalDays, usedDays, availableDays)
    }

    fun getUsedVacationDaysForTimePeriod(
        employeeEmail: String,
        startDateString: String,
        endDateString: String
    ): List<Vacation> {
        val employee = employeeService.findByEmail(employeeEmail)

        val formatter = SimpleDateFormat(DATE_FORMAT)
        val startDate: Date
        val endDate: Date
        try {
            startDate = formatter.parse(startDateString)
            endDate = formatter.parse(endDateString)
        } catch (ex: ParseException) {
            throw InvalidDate(INVALID_DATE_FORMAT)
        }

        if (startDate > endDate) {
            throw InvalidDate(INVALID_DATE_ORDER)
        }

        return vacationService.findRecordsForTimePeriod(employee, startDate, endDate)
    }

    fun insertNewRecord(employeeEmail: String, startDateString: String, endDateString: String): Long {
        val employee = employeeService.findByEmail(employeeEmail)

        val formatter = SimpleDateFormat(DATE_FORMAT)
        val startDate: Date
        val endDate: Date
        try {
            startDate = formatter.parse(startDateString)
            endDate = formatter.parse(endDateString)
        } catch (ex: ParseException) {
            throw InvalidDate(INVALID_DATE_FORMAT)
        }

        if (startDate > endDate) {
            throw InvalidDate(INVALID_DATE_ORDER)
        }
        if (startDate > Date() || endDate > Date()) {
            throw InvalidDate(INVALID_DATE_TIME)
        }

        val vacation = Vacation(startDate = startDate, endDate = endDate, employee = employee)

        return vacationService.save(vacation)
    }
}