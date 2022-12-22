package com.vacationtracker.employee.repositories

import com.vacationtracker.employee.models.Employee
import com.vacationtracker.employee.models.Vacation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface VacationRepository : JpaRepository<Vacation, Long> {
    fun countAllByEmployee(employee: Employee): Int

    @Query("SELECT v FROM Vacation v WHERE v.employee = :employee AND (v.startDate BETWEEN :startDate AND :endDate) OR (v.endDate BETWEEN :startDate AND :endDate)")
    fun findRecordsForTimePeriod(
        employee: Employee, startDate: LocalDate, endDate: LocalDate
    ): List<Vacation>
}