package com.vacationtracker.datasearch.controller

import com.vacationtracker.database.model.Vacation
import com.vacationtracker.datasearch.service.DataSearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class DataSearchController {

    companion object Constants {
        private const val IMPORT_SUCCESSFUL = "Import successful."
        private const val DATE_FORMAT: String = "yyyy-MM-dd"
    }
    @Autowired
    private lateinit var dataSearchService: DataSearchService

    @GetMapping("/getVacationDaysPerYear")
    fun getVacationDaysPerYear(@RequestParam employeeEmail: String, @RequestParam year: Int): ResponseEntity<String> {
        val vacationDaysList = dataSearchService.getVacationDaysPerYear(employeeEmail, year)
        val totalDays = vacationDaysList[0]
        val usedDays = vacationDaysList[1]
        val availableDays = vacationDaysList[2]

        return ResponseEntity.ok("Total days: $totalDays; Used days: $usedDays; Available days: $availableDays.")
    }

    @GetMapping("/getUsedVacationDaysForTimePeriod")
    fun getUsedVacationDaysForTimePeriod(
        @RequestParam employeeEmail: String, @RequestParam startDate: String, @RequestParam endDate: String
    ): ResponseEntity<List<Vacation>> {
        val vacations = dataSearchService.getUsedVacationDaysForTimePeriod(employeeEmail, startDate, endDate)

        return ResponseEntity.ok(vacations)
    }

    @PostMapping("/insertNewRecord")
    fun insertNewRecord(
        @RequestParam employeeEmail: String, @RequestParam startDate: String, @RequestParam endDate: String
    ): ResponseEntity<String> {
        val vacationId = dataSearchService.insertNewRecord(employeeEmail, startDate, endDate)

        return ResponseEntity.ok("$IMPORT_SUCCESSFUL New vacation ID: $vacationId")
    }
}