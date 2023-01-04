package com.vacationtracker.datasearch.service

import com.vacationtracker.database.model.Vacation
import com.vacationtracker.database.service.implementation.EmployeeService
import com.vacationtracker.database.service.implementation.VacationService
import com.vacationtracker.datasearch.exception.InvalidDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


@Service
class DataSearchService {

    companion object Constants {
        private const val INVALID_DATE_FORMAT = "Invalid date format."
        private const val INVALID_DATE_ORDER = "Start date must be before or same as end date."
        private const val INVALID_DATE_TIME = "Dates should be before or equal as current date."
        private const val INVALID_VACATION_DURATION = "Not enough vacation days available."
        private const val DATE_FORMAT = "yyyy-MM-dd"
    }

    @Autowired
    private lateinit var employeeService: EmployeeService

    @Autowired
    private lateinit var vacationService: VacationService

    private val formatter = SimpleDateFormat(DATE_FORMAT)

    fun getVacationDaysPerYear(employeeEmail: String, year: Int): List<Int> {
        val employee = employeeService.findByEmail(employeeEmail)
        val totalDays = employeeService.getVacationDaysPerYear(employee, year)
        val usedVacations = vacationService.findUsedVacationsPerYear(employee, year)

        var usedDays: Long = 0
        usedVacations.forEach { vacation ->
            val currentStartYear = getDateYear(vacation.startDate)
            val currentEndYear = getDateYear(vacation.endDate)

            usedDays += if (currentStartYear == currentEndYear) {
                getDifferenceInDays(vacation.startDate, vacation.endDate)
            } else {
                val nextYearJanuary1 = formatter.parse((currentStartYear + 1).toString() + "-01-01")
                getDifferenceInDays(vacation.startDate, nextYearJanuary1)
            }
        }

        val availableDays = totalDays - usedDays

        return listOf(totalDays, usedDays.toInt(), availableDays.toInt())
    }

    fun getUsedVacationDaysForTimePeriod(
        employeeEmail: String, startDateString: String, endDateString: String
    ): List<Vacation> {
        val employee = employeeService.findByEmail(employeeEmail)

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

        val newVacationStartDateYear = getDateYear(startDate)
        val newVacationEndDateYear = getDateYear(endDate)
        val usedVacationDaysPerYear: MutableMap<Int, Long> = HashMap()

        for (newVacationYear in newVacationStartDateYear..newVacationEndDateYear) {
            val usedVacationsPerYear = vacationService.findUsedVacationsPerYear(employee, newVacationYear)
            usedVacationsPerYear.forEach { vacation ->
                val currentStartYear = getDateYear(vacation.startDate)
                val currentEndYear = getDateYear(vacation.endDate)

                if (currentStartYear == currentEndYear) {
                    if (!usedVacationDaysPerYear.containsKey(newVacationYear)) {
                        usedVacationDaysPerYear[newVacationYear] = getDifferenceInDays(
                            vacation.startDate, vacation.endDate
                        )
                    } else {
                        usedVacationDaysPerYear[newVacationYear] =
                            usedVacationDaysPerYear[newVacationYear]!! + getDifferenceInDays(
                                vacation.startDate, vacation.endDate
                            )
                    }
                } else {
                    for (currentVacationYear in currentStartYear..currentEndYear) {
                        val daysToAdd = when (currentVacationYear) {
                            currentStartYear -> {
                                val nextYearJanuary1 = formatter.parse((currentStartYear + 1).toString() + "-01-01")
                                getDifferenceInDays(vacation.startDate, nextYearJanuary1)
                            }

                            currentEndYear -> {
                                val thisYearJanuary1 = formatter.parse("$currentEndYear-01-01")
                                getDifferenceInDays(vacation.endDate, thisYearJanuary1)
                            }

                            else -> {
                                if (Year.isLeap(currentVacationYear.toLong())) 366 else 365
                            }
                        }
                        usedVacationDaysPerYear[currentVacationYear] =
                            usedVacationDaysPerYear[currentVacationYear]!! + daysToAdd
                    }
                }
            }

            val daysToAdd = if (newVacationStartDateYear == newVacationEndDateYear) {
                getDifferenceInDays(startDate, endDate)
            } else if (newVacationYear == newVacationStartDateYear) {
                val nextYearJanuary1 = formatter.parse((newVacationYear + 1).toString() + "-01-01")
                getDifferenceInDays(startDate, nextYearJanuary1)
            } else if (newVacationYear == newVacationEndDateYear) {
                val thisYearJanuary1 = formatter.parse("$newVacationYear-01-01")
                getDifferenceInDays(endDate, thisYearJanuary1)
            } else {
                if (Year.isLeap(newVacationYear.toLong())) 366 else 365
            }

            val usedDays =
                if (usedVacationDaysPerYear.containsKey(newVacationYear)) usedVacationDaysPerYear[newVacationStartDateYear] else 0
            val availableDays =
                if (employee.totalVacationDays.containsKey(newVacationYear)) employee.totalVacationDays[newVacationYear] else 0

            if (usedDays!! + daysToAdd > availableDays!!) {
                throw InvalidDate(INVALID_VACATION_DURATION)
            }
        }

        val vacation = Vacation(startDate = startDate, endDate = endDate, employee = employee)

        return vacationService.save(vacation)
    }

    private fun getDifferenceInDays(startDate: Date, endDate: Date): Long {
        val differenceInMilliseconds = abs(endDate.time - startDate.time)
        return TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS)
    }

    private fun getDateYear(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.YEAR)
    }
}