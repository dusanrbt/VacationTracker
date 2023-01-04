package com.vacationtracker.datasearch.controller

import com.vacationtracker.database.dto.ExceptionDTO
import com.vacationtracker.database.dto.MessageDTO
import com.vacationtracker.database.dto.VacationDaysDTO
import com.vacationtracker.database.model.Vacation
import com.vacationtracker.datasearch.dto.VacationRequestDTO
import com.vacationtracker.datasearch.service.DataSearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
class DataSearchController {

    @Autowired
    private lateinit var dataSearchService: DataSearchService

    @GetMapping("/getVacationDaysPerYear")
    fun getVacationDaysPerYear(principal: Principal, @RequestParam year: Int): ResponseEntity<VacationDaysDTO> {
        val vacationDays = dataSearchService.getVacationDaysPerYear(principal.name, year)

        return ResponseEntity.ok(
            VacationDaysDTO(
                totalDays = vacationDays[0], usedDays = vacationDays[1], availableDays = vacationDays[2]
            )
        )
    }

    @GetMapping("/getUsedVacationDaysForTimePeriod")
    fun getUsedVacationDaysForTimePeriod(
        principal: Principal, @RequestParam startDate: String, @RequestParam endDate: String
    ): ResponseEntity<List<Vacation>> {
        return ResponseEntity.ok(dataSearchService.getUsedVacationDaysForTimePeriod(principal.name, startDate, endDate))
    }

    @PostMapping("/insertNewRecord")
    fun insertNewRecord(
        principal: Principal, @RequestBody vacationRequestDTO: VacationRequestDTO
    ): ResponseEntity<MessageDTO> {
        val vacationId =
            dataSearchService.insertNewRecord(principal.name, vacationRequestDTO.startDate, vacationRequestDTO.endDate)

        return ResponseEntity.ok(MessageDTO("New vacation ID: $vacationId"))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ExceptionDTO> {
        return ResponseEntity.badRequest()
            .body(ExceptionDTO(message = exception.message!!, status = 400, statusText = "Bad request"))
    }
}