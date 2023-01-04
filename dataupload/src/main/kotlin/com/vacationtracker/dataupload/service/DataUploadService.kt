package com.vacationtracker.dataupload.service

import com.vacationtracker.database.exception.EmployeeNotFound
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Service
class DataUploadService {

    companion object Constants {
        private const val IMPORT_SUCCESSFUL = "Import successful."
        private const val FILE_EMPTY = "File is empty."
        private const val FILE_NOT_UPLOADED = "File not uploaded."
        private const val ALLOWED_FILE_EXTENSION = "csv"
        private const val USER_NON_EXISTENT = "User does not exist: line "
        private const val INVALID_FILE_EXTENSION = "File extension must be: .$ALLOWED_FILE_EXTENSION"
        private const val INVALID_YEAR_FORMAT = "Invalid year format, Int expected."
        private const val INVALID_NUMBER_OF_DAYS_FORMAT = "Invalid number of days format, Int expected."
        private const val INVALID_COLUMN_NAMES = "File has invalid column names."
        private const val INVALID_COLUMN_NUMBER = "Invalid number of columns: line "
        private const val INVALID_EMAIL_FORMAT = "Wrong email format: line "
        private const val INVALID_PASSWORD_FORMAT = "Empty password: line "
        private const val INVALID_HEADER_AVAILABLE_VACATIONS = "Invalid header value, 'Vacation year,[year]' expected."
        private const val INVALID_DATE_FORMAT = "Invalid date format: line "
        private const val INVALID_DATE_ORDER = "Start date must be before or same as end date."
        private const val UNIQUE_COLUMN_VALUE = "Employee with this email already exists."
        private const val DATE_FORMAT = "EEE, MMM d, yyyy"
        private const val EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
    }

    @Autowired
    private lateinit var employeeService: EmployeeService

    @Autowired
    private lateinit var vacationService: VacationService

    fun importEmployeeProfiles(file: MultipartFile): String {
        isFileUploaded(file)
        checkFileExtension(file)

        val reader = BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8))
        val format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build()
        val parser = CSVParser(reader, format)
        val records = parser.records

        if (records.isEmpty()) {
            throw CSVException(FILE_EMPTY)
        }

        val columnNames = records[0]

        if (columnNames.size() != 2) {
            throw CSVException(INVALID_COLUMN_NUMBER + 2)
        }

        if (!(columnNames.get(0).equals("Employee Email") && columnNames.get(1).equals("Employee Password"))) {
            throw CSVException(INVALID_COLUMN_NAMES)
        }

        val employeeList: MutableList<Employee> = mutableListOf()
        var errorList = ""

        records.forEach {
            if (it.recordNumber == 1L) {
                return@forEach
            }

            val numberOfColumns = it.size()

            if (numberOfColumns != 2) {
                errorList += (INVALID_COLUMN_NUMBER + it.recordNumber + ";")
                return@forEach
            }

            val email = it.get(0)
            val password = it.get(1)

            if (!(email matches Regex(EMAIL_REGEX))) {
                errorList += (INVALID_EMAIL_FORMAT + it.recordNumber + ";")
                return@forEach
            }
            if (password.equals("")) {
                errorList += (INVALID_PASSWORD_FORMAT + it.recordNumber + ";")
                return@forEach
            }

            employeeList += Employee(email = email, password = password)
        }

        try {
            employeeService.saveAll(employeeList)
        } catch (ex: Exception) {
            throw Exception(UNIQUE_COLUMN_VALUE)
        }

        return errorList.ifEmpty { IMPORT_SUCCESSFUL }
    }

    fun importUsedVacations(file: MultipartFile): String {
        isFileUploaded(file)
        checkFileExtension(file)

        val reader = BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8))
        val format = CSVFormat.DEFAULT
        val parser = CSVParser(reader, format)
        val records = parser.records

        if (records.isEmpty()) {
            throw CSVException(FILE_EMPTY)
        }

        val columnNames = records[0]

        if (columnNames.size() != 3) {
            throw CSVException(INVALID_COLUMN_NUMBER + 1)
        }

        if (!(columnNames.get(0).equals("Employee") && columnNames.get(1)
                .equals("Vacation start date") && columnNames.get(2).equals("Vacation end date"))
        ) {
            throw CSVException(INVALID_COLUMN_NAMES)
        }

        val vacationList: MutableList<Vacation> = mutableListOf()
        var errorList = ""

        records.forEach {
            if (it.recordNumber == 1L) {
                return@forEach
            }

            val numberOfColumns = it.size()

            if (numberOfColumns != 3) {
                errorList += (INVALID_COLUMN_NUMBER + it.recordNumber + ";")
                return@forEach
            }

            val employeeEmail = it.get(0)
            val startDateString = it.get(1)
            val endDateString = it.get(2)

            if (!(employeeEmail matches Regex(EMAIL_REGEX))) {
                errorList += (INVALID_EMAIL_FORMAT + it.recordNumber + ";")
                return@forEach
            }

            val employee: Employee
            try {
                employee = employeeService.findByEmail(employeeEmail)
            } catch (ex: EmployeeNotFound) {
                errorList += (USER_NON_EXISTENT + it.recordNumber + ";")
                return@forEach
            }

            val formatter = SimpleDateFormat(DATE_FORMAT)

            val startDate: Date
            val endDate: Date
            try {
                startDate = formatter.parse(startDateString)
                endDate = formatter.parse(endDateString)
            } catch (ex: ParseException) {
                errorList += (INVALID_DATE_FORMAT + it.recordNumber + ";")
                return@forEach
            }

            if (startDate > endDate) {
                errorList += (INVALID_DATE_ORDER + it.recordNumber + ";")
                return@forEach
            }

            vacationList += Vacation(startDate = startDate, endDate = endDate, employee = employee)
        }

        vacationService.saveAll(vacationList)

        return errorList.ifEmpty { IMPORT_SUCCESSFUL }
    }

    fun importAvailableVacationDaysPerYear(file: MultipartFile): String {
        isFileUploaded(file)
        checkFileExtension(file)

        val reader = BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8))
        val format = CSVFormat.DEFAULT
        val parser = CSVParser(reader, format)
        val records = parser.records

        if (records.isEmpty()) {
            throw CSVException(FILE_EMPTY)
        }

        val header = records[0]

        if (header.size() != 2) {
            throw CSVException(INVALID_COLUMN_NUMBER + 1)
        }
        if (!(header.get(0).equals("Vacation year"))) {
            throw CSVException(INVALID_HEADER_AVAILABLE_VACATIONS)
        }
        if (header.get(1).toIntOrNull() == null) {
            throw CSVException(INVALID_YEAR_FORMAT)
        }

        val year = header.get(1).toInt()

        val columnNames = records[1]

        if (columnNames.size() != 2) {
            throw CSVException(INVALID_COLUMN_NUMBER + 1)
        }
        if (!(columnNames.get(0).equals("Employee") && columnNames.get(1).equals("Total vacation days"))) {
            throw CSVException(INVALID_COLUMN_NAMES)
        }

        var errorList = ""

        records.forEach {
            if (it.recordNumber < 3L) {
                return@forEach
            }

            val numberOfColumns = it.size()

            if (numberOfColumns != 2) {
                errorList += (INVALID_COLUMN_NUMBER + it.recordNumber + ";")
                return@forEach
            }

            val employeeEmail = it.get(0)
            val availableVacationDays = it.get(1)

            if (!(employeeEmail matches Regex(EMAIL_REGEX))) {
                errorList += (INVALID_EMAIL_FORMAT + it.recordNumber + ";")
                return@forEach
            }
            if (availableVacationDays.toIntOrNull() == null) {
                errorList += (INVALID_NUMBER_OF_DAYS_FORMAT + it.recordNumber + ";")
                return@forEach
            }

            if (it.recordNumber > 2L) {
                var employee: Employee
                try {
                    employee = employeeService.findByEmail(employeeEmail)
                } catch (ex: EmployeeNotFound) {
                    errorList += (USER_NON_EXISTENT + it.recordNumber + ";")
                    return@forEach
                }
                employee.totalVacationDays[year] = availableVacationDays.toInt()
                employeeService.save(employee)
            }
        }

        return errorList.ifEmpty { IMPORT_SUCCESSFUL }
    }

    fun clearDatabase() {
        vacationService.deleteAll()
        employeeService.deleteAll()
    }

    private fun checkFileExtension(file: MultipartFile) {
        val fileExtension = FilenameUtils.getExtension(file.originalFilename)

        if (fileExtension != ALLOWED_FILE_EXTENSION) {
            throw CSVException(INVALID_FILE_EXTENSION)
        }
    }

    private fun isFileUploaded(file: MultipartFile) {
        if (file.isEmpty) {
            throw CSVException(FILE_NOT_UPLOADED)
        }
    }
}