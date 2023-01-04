package com.vacationtracker.database.service.implementation

import com.vacationtracker.database.model.Employee
import com.vacationtracker.database.model.Vacation
import com.vacationtracker.database.repository.VacationRepository
import com.vacationtracker.database.service.interfaces.IVacationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class VacationService : IVacationService {

    @Autowired
    private lateinit var vacationRepository: VacationRepository

    override fun saveAll(vacationList: List<Vacation>): Int {
        val vacationsAdded = vacationRepository.saveAll(vacationList)

        return vacationsAdded.size
    }

    override fun save(vacation: Vacation): Long {
        val vacation = vacationRepository.save(vacation)

        return vacation.id
    }

    override fun deleteAll() {
        vacationRepository.deleteAll()
    }

    override fun countAllByEmployee(employee: Employee): Int {
        return vacationRepository.countAllByEmployee(employee)
    }

    override fun findUsedVacationsPerYear(employee: Employee, year: Int): List<Vacation> {
        return vacationRepository.findUsedVacationsPerYear(employee, year)
    }

    override fun findRecordsForTimePeriod(employee: Employee, startDate: Date, endDate: Date): List<Vacation> {
        return vacationRepository.findRecordsForTimePeriod(employee, startDate, endDate)
    }


}