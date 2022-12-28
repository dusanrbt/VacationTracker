package com.vacationtracker.dataupload.service

import com.vacationtracker.database.model.Employee
import com.vacationtracker.database.model.Vacation
import com.vacationtracker.database.service.implementation.EmployeeService
import com.vacationtracker.database.service.implementation.VacationService
import com.vacationtracker.dataupload.exception.CSVException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.io.FilenameUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat

@Service
class DataUploadService {

    companion object Constants {
        private const val ALLOWED_FILE_EXTENSION = "csv"
        private const val FILE_WRONG_EXTENSION_MSG = "File extension must be $ALLOWED_FILE_EXTENSION"
        private const val FILE_EMPTY_MSG = "File is empty."
        private const val WRONG_ROW_FORMAT_MSG = "Wrong row format: line "
        private const val WRONG_YEAR_FORMAT_MSG = "Wrong year format, Int expected."
        private const val DATE_FORMAT = "EEE, MMM d, yyyy"
        private const val EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
    }

    @Autowired
    private lateinit var employeeService: EmployeeService

    @Autowired
    private lateinit var vacationService: VacationService

    fun importEmployeeProfiles(file: MultipartFile): Int {
        val fileExtension = FilenameUtils.getExtension(file.originalFilename)

        if (fileExtension != ALLOWED_FILE_EXTENSION) {
            throw CSVException(FILE_WRONG_EXTENSION_MSG)
        }

        val reader = BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8))
        val format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build()
        val parser = CSVParser(reader, format)
        val records = parser.records

        if (records.isEmpty()) {
            throw CSVException(FILE_EMPTY_MSG)
        }

        val employeeList: MutableList<Employee> = mutableListOf()

        records.forEach {
            val email = it.get(0)
            val password = it.get(1)

            if (email.equals("") || password.equals("")) {
                throw CSVException(WRONG_ROW_FORMAT_MSG + it.recordNumber)
            }
            if (it.recordNumber != 1L) {
                employeeList += Employee(email = email, password = password)
            }
        }

        return employeeService.saveAll(employeeList)
    }

    fun importUsedVacations(file: MultipartFile): Int {
        val fileExtension = FilenameUtils.getExtension(file.originalFilename)

        if (fileExtension != ALLOWED_FILE_EXTENSION) {
            throw CSVException(FILE_WRONG_EXTENSION_MSG)
        }

        val reader = BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8))
        val format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build()
        val parser = CSVParser(reader, format)
        val records = parser.records

        if (records.isEmpty()) {
            throw CSVException(FILE_EMPTY_MSG)
        }

        val vacationList: MutableList<Vacation> = mutableListOf()

        records.forEach {
            val employeeEmail = it.get(0)
            val startDateString = it.get(1)
            val endDateString = it.get(2)

            if (employeeEmail.equals("") || startDateString.equals("") || endDateString.equals("")) {
                throw CSVException(WRONG_ROW_FORMAT_MSG + it.recordNumber)
            }

            val employee = employeeService.findByEmail(employeeEmail)

            val formatter = SimpleDateFormat(DATE_FORMAT)
            val startDate = formatter.parse(startDateString)
            val endDate = formatter.parse(endDateString)

            vacationList += Vacation(startDate = startDate, endDate = endDate, employee = employee)
        }

        return vacationService.saveAll(vacationList)
    }

    fun importAvailableVacationDaysPerYear(file: MultipartFile): Int {
        val fileExtension = FilenameUtils.getExtension(file.originalFilename)

        if (fileExtension != ALLOWED_FILE_EXTENSION) {
            throw CSVException(FILE_WRONG_EXTENSION_MSG)
        }

        val reader = BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8))
        val format = CSVFormat.DEFAULT
        val parser = CSVParser(reader, format)
        val records = parser.records

        if (records.isEmpty()) {
            throw CSVException(FILE_EMPTY_MSG)
        }

        val yearString = records[0].get(1)

        if (yearString.equals("") || yearString.toIntOrNull() == null) {
            throw CSVException(WRONG_YEAR_FORMAT_MSG)
        }

        val year = yearString.toInt()

        var rowsAdded = 0

        records.forEach {
            val employeeEmail = it.get(0)
            val availableVacationDays = it.get(1)

            if (employeeEmail.equals("") || availableVacationDays.equals("")) {
                throw CSVException(WRONG_ROW_FORMAT_MSG + it.recordNumber)
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