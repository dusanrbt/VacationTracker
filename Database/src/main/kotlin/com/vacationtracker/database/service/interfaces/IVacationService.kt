package com.vacationtracker.database.service.interfaces

import com.vacationtracker.database.model.Employee
import com.vacationtracker.database.model.Vacation
import org.springframework.stereotype.Service
import java.util.*

@Service
interface IVacationService {
    fun saveAll(vacationList: List<Vacation>): Int

    fun save(vacation: Vacation): Long

    fun deleteAll()

    fun countAllByEmployee(employee: Employee): Int

    fun findUsedVacationsPerYear(employee: Employee, year: Int): List<Vacation>

    fun findRecordsForTimePeriod(employee: Employee, startDate: Date, endDate: Date): List<Vacation>
}