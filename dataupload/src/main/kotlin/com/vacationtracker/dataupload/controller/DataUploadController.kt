package com.vacationtracker.dataupload.controller

import com.vacationtracker.database.dto.ExceptionDTO
import com.vacationtracker.database.dto.MessageDTO
import com.vacationtracker.dataupload.service.DataUploadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
class DataUploadController {

    @Autowired
    private lateinit var dataUploadService: DataUploadService

    @PostMapping("/importEmployeeProfiles")
    fun importEmployeeProfiles(@RequestParam file: MultipartFile): ResponseEntity<MessageDTO> {
        return ResponseEntity.ok(MessageDTO(dataUploadService.importEmployeeProfiles(file)))
    }

    @PostMapping("/importUsedVacations")
    fun importUsedVacations(@RequestParam file: MultipartFile): ResponseEntity<MessageDTO> {
        return ResponseEntity.ok(MessageDTO(dataUploadService.importUsedVacations(file)))
    }

    @PostMapping("/importAvailableVacationDaysPerYear")
    fun importAvailableVacationDaysPerYear(@RequestParam file: MultipartFile): ResponseEntity<MessageDTO> {
        return ResponseEntity.ok(MessageDTO(dataUploadService.importAvailableVacationDaysPerYear(file)))
    }

    @PostMapping("/clearDatabase")
    fun clearDatabase() {
        dataUploadService.clearDatabase()
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ExceptionDTO> {
        return ResponseEntity.badRequest()
            .body(ExceptionDTO(message = exception.message!!, status = 400, statusText = "Bad request"))
    }
}