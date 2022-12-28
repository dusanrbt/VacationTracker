package com.vacationtracker.dataupload.controller

import com.vacationtracker.dataupload.service.DataUploadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
class DataUploadController {

    companion object Constants {
        private const val IMPORT_SUCCESSFUL_MSG: String = "Import successful."
    }

    @Autowired
    private lateinit var dataUploadService: DataUploadService

    @PostMapping("/importEmployeeProfiles")
    fun importEmployeeProfiles(@RequestParam file: MultipartFile): ResponseEntity<String> {
        val employeesCount = dataUploadService.importEmployeeProfiles(file)

        return ResponseEntity.ok("$IMPORT_SUCCESSFUL_MSG Number of employees: $employeesCount")
    }

    @PostMapping("/importUsedVacations")
    fun importUsedVacations(@RequestParam file: MultipartFile): ResponseEntity<String> {
        val usedVacationsCount = dataUploadService.importUsedVacations(file)

        return ResponseEntity.ok("$IMPORT_SUCCESSFUL_MSG Number of used vacations: $usedVacationsCount")
    }

    @PostMapping("/importAvailableVacationDaysPerYear")
    fun importAvailableVacationDaysPerYear(@RequestParam file: MultipartFile): ResponseEntity<String> {
        val availableVacationsCount = dataUploadService.importAvailableVacationDaysPerYear(file)

        return ResponseEntity.ok("$IMPORT_SUCCESSFUL_MSG Number of available vacations: $availableVacationsCount")
    }
}