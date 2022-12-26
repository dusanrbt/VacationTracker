package com.vacationtracker.dataupload.service

import com.vacationtracker.dataupload.exception.CSVException
import com.vacationtracker.database.model.Employee
import com.vacationtracker.database.model.Vacation
import com.vacationtracker.database.service.implementation.EmployeeService
import com.vacationtracker.database.service.implementation.VacationService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat

@Service
class DataUploadService {

    @Autowired
    private lateinit var employeeService: EmployeeService

    @Autowired
    private lateinit var vacationService: VacationService

    fun importEmployeeProfiles(file: MultipartFile): Int {
        val reader = BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8))
        val format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build()
        val parser = CSVParser(reader, format)
        val records = parser.records

        if (records.isEmpty()) {
            throw CSVException("File is empty.")
        }

        val employeeList: MutableList<Employee> = mutableListOf()

        records.forEach {
            val email = it.get(0)
            val password = it.get(1)

            if (email.equals("") || password.equals("")) {
                throw CSVException("Wrong format of a row in line " + it.recordNumber + ".")
            }
            if (it.recordNumber != 1L) {
                employeeList += Employee(email = email, password = password)
            }
        }

        return employeeService.saveAll(employeeList)
    }

    fun importUsedVacations(file: MultipartFile): Int {
        val reader = BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8))
        val format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build()
        val parser = CSVParser(reader, format)
        val records = parser.records

        if (records.isEmpty()) {
            throw CSVException("File is empty.")
        }

        val vacationList: MutableList<Vacation> = mutableListOf()

        records.forEach {
            val employeeEmail = it.get(0)
            val startDateString = it.get(1)
            val endDateString = it.get(2)

            if (employeeEmail.equals("") || startDateString.equals("") || endDateString.equals("")) {
                throw CSVException("Wrong format of a row in line " + it.recordNumber + ".")
            }

            val employee = employeeService.findByEmail(employeeEmail)
            val formatter = SimpleDateFormat("EEE, MMM d, yyyy")
            val startDate = formatter.parse(startDateString)
            val endDate = formatter.parse(endDateString)

            vacationList += Vacation(startDate = startDate, endDate = endDate, employee = employee)
        }

        return vacationService.saveAll(vacationList)
    }

    fun importAvailableVacationDaysPerYear(file: MultipartFile): Int {
        val reader = BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8))
        val format = CSVFormat.DEFAULT
        val parser = CSVParser(reader, format)
        val records = parser.records

        if (records.isEmpty()) {
            throw CSVException("File is empty.")
        }

        val yearString = records[0].get(1)

        if (yearString.equals("") || yearString.toIntOrNull() == null) {
            throw CSVException("Wrong year format.")
        }

        val year = yearString.toInt()

        var rowsAdded = 0

        records.forEach {
            val employeeEmail = it.get(0)
            val availableVacationDays = it.get(1)

            if (employeeEmail.equals("") || availableVacationDays.equals("")) {
                throw CSVException("Wrong format of a row in line " + it.recordNumber + ".")
            }
            if (it.recordNumber > 2L) {
                val employee = employeeService.findByEmail(employeeEmail)
                employee.totalVacationDays[year] = availableVacationDays.toInt()
                employeeService.save(employee)
                rowsAdded++
            }
        }

        return rowsAdded
    }
}