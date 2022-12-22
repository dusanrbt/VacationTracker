package com.vacationtracker.admin.services

import com.vacationtracker.admin.models.Employee
import com.vacationtracker.admin.models.Vacation
import com.vacationtracker.admin.repositories.EmployeeRepository
import com.vacationtracker.admin.repositories.VacationRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.time.LocalDate

@Service
class AdminService(
    private val employeeRepository: EmployeeRepository,
    private val vacationRepository: VacationRepository
) {
    fun insertEmployees(multipartFile: MultipartFile) {
        val employees: MutableList<Employee> = mutableListOf()
        var skipLineCounter = 0

        val inputStream = multipartFile.inputStream
        BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().forEach { line ->
            if (skipLineCounter < 2) {
                skipLineCounter++
            } else {
                val lineSplit = line.split(",")

                val email = lineSplit[0]
                val password = lineSplit[1]

                employees += Employee(email = email, password = password)
            }
        }
        employeeRepository.saveAll(employees)
    }

    fun getMonthDay(monthName: String): Int {
        when (monthName) {
            "January" -> return 1
            "February" -> return 2
            "March" -> return 3
            "April" -> return 4
            "May" -> return 5
            "Jun" -> return 6
            "July" -> return 7
            "August" -> return 8
            "September" -> return 9
            "October" -> return 10
            "November" -> return 11
            "December" -> return 12
        }

        // Never happens
        return 1
    }

    fun insertVacations(multipartFile: MultipartFile) {
        val vacations: MutableList<Vacation> = mutableListOf()
        var skipLineCounter = 0

        val inputStream = multipartFile.inputStream
        BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().forEach { line ->
            if (skipLineCounter < 1) {
                skipLineCounter++
            } else {
                val lineSplit = line.split("\"")

                val employeeEmail = lineSplit[0].split(",")[0]

                // Format: Friday, August 30, 2019
                val startDateArray = lineSplit[1].split(", ")
                val startDateMonthAndDayArray = startDateArray[1].split(" ")
                val startDateMonth = startDateMonthAndDayArray[0]
                val startDateDay = startDateMonthAndDayArray[1]
                val startDateYear = startDateArray[2]
                val startDate = LocalDate.of(startDateYear.toInt(), getMonthDay(startDateMonth), startDateDay.toInt())

                val endDateArray = lineSplit[3].split(", ")
                val endDateMonthAndDayArray = endDateArray[1].split(" ")
                val endDateMonth = endDateMonthAndDayArray[0]
                val endDateDay = endDateMonthAndDayArray[1]
                val endDateYear = endDateArray[2]
                val endDate = LocalDate.of(endDateYear.toInt(), getMonthDay(endDateMonth), endDateDay.toInt())

                val employee = employeeRepository.findByEmail(employeeEmail).get()

                vacations += Vacation(startDate = startDate, endDate = endDate, employee = employee)
            }
        }

        vacationRepository.saveAll(vacations)
    }

    fun updateEmployeeVacationDays(multipartFile: MultipartFile) {
        var skipLineCounter = 0
        var year: Int = -1

        val inputStream = multipartFile.inputStream
        BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().forEach { line ->
            if (skipLineCounter < 2) {
                if (skipLineCounter == 0) {
                    val lineSplit = line.split(",")
                    year = lineSplit[1].toInt()
                }
                skipLineCounter++
            } else {
                val lineSplit = line.split(",")

                val employeeEmail = lineSplit[0].split(",")[0]
                val totalVacationDays = lineSplit[1].toInt()

                val employee = employeeRepository.findByEmail(employeeEmail).get()

                employee.totalVacationDays[year] = totalVacationDays

                employeeRepository.save(employee)
            }
        }
    }

}