package com.vacationtracker.employee.controllers

import com.vacationtracker.employee.models.Vacation
import com.vacationtracker.employee.services.EmployeeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/api/employee"])
class EmployeeController(private val employeeService: EmployeeService) {

    @GetMapping
    fun index(): ResponseEntity<String> {
        return ResponseEntity.ok("Employee works!")
    }

    @GetMapping("/getVacationDaysPerYear")
    fun getVacationDaysPerYear(@RequestParam employeeEmail: String, @RequestParam year: Int): ResponseEntity<String> {
        val vacationDaysList = employeeService.getVacationDaysPerYear(employeeEmail, year)
        val totalDays = vacationDaysList[0]
        val usedDays = vacationDaysList[1]
        val availableDays = vacationDaysList[2]

        return ResponseEntity.ok("Total days: $totalDays; Used days: $usedDays; Available days: $availableDays.")
    }

    @GetMapping("/getUsedVacationDaysForTimePeriod")
    fun getUsedVacationDaysForTimePeriod(
        @RequestParam employeeEmail: String, @RequestParam startDate: String, @RequestParam endDate: String
    ): ResponseEntity<List<Vacation>> {
        val vacations = employeeService.getUsedVacationDaysForTimePeriod(employeeEmail, startDate, endDate)

        return ResponseEntity.ok(vacations)
    }

    @PostMapping("/insertNewRecord")
    fun insertNewRecord(
        @RequestParam employeeEmail: String, @RequestParam startDate: String, @RequestParam endDate: String
    ): ResponseEntity<String> {
        val vacation = employeeService.insertNewRecord(employeeEmail, startDate, endDate)

        return ResponseEntity.ok("Import successful! Vacation ID is: " + vacation.id)
    }
}