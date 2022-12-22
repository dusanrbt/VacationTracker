package com.vacationtracker.admin.controllers

import com.vacationtracker.admin.services.AdminService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping(path = ["/api/admin"])
class AdminController(private val adminService: AdminService) {

    @GetMapping
    fun index(): String? {
        return "Admin works!"
    }

    @PostMapping("/importEmployees")
    fun importEmployees(@RequestParam("file") multipartFile: MultipartFile): ResponseEntity<String> {
        adminService.insertEmployees(multipartFile)

        return ResponseEntity.ok("Import successful!")
    }

    @PostMapping("/importVacations")
    fun importVacations(@RequestParam("file") multipartFile: MultipartFile): ResponseEntity<String> {
        adminService.insertVacations(multipartFile)

        return ResponseEntity.ok("Import successful!")
    }

    @PostMapping("/importVacationDays")
    fun importVacationDays(@RequestParam("file") multipartFile: MultipartFile): ResponseEntity<String> {
        adminService.updateEmployeeVacationDays(multipartFile)

        return ResponseEntity.ok("Import successful!")
    }

    /*@GetMapping("/importEmployeesGET")
    fun importEmployeesGET(): String {
        val fileName = "/static/assets/employee_profiles.csv"
        val file = File(javaClass.getResource(fileName).file)
        val lines: List<String> = file.readLines()
        val delimiter = ","
        var cnt = 0
        val employees: MutableList<Employee> = mutableListOf()
        lines.forEach { line ->
            if (cnt < 2) {
                cnt++
            } else {
                val lineSplit = line.split(delimiter)
                val email = lineSplit[0]
                val password = lineSplit[1]
                val employee = Employee(email, password)

                employees += employee
            }
        }

        adminService.addNewEmployees(employees)

        return "Import successful!"
    }*/
}