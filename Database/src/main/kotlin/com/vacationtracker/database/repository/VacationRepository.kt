package com.vacationtracker.database.repository

import com.vacationtracker.database.model.Employee
import com.vacationtracker.database.model.Vacation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VacationRepository : JpaRepository<Vacation, Long> {
    fun countAllByEmployee(employee: Employee): Int

    @Query("SELECT v FROM Vacation v WHERE v.employee = :employee AND EXTRACT(YEAR FROM v.startDate) = :year")
    fun findUsedVacationsPerYear(employee: Employee, year: Int): List<Vacation>

    @Query("SELECT v FROM Vacation v WHERE v.employee = :employee AND (v.startDate BETWEEN :startDate AND :endDate) OR (v.endDate BETWEEN :startDate AND :endDate)")
    fun findRecordsForTimePeriod(employee: Employee, startDate: Date, endDate: Date): List<Vacation>
}