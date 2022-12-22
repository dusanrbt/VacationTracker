package com.vacationtracker.admin.repositories

import com.vacationtracker.admin.models.Vacation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VacationRepository : JpaRepository<Vacation, Long>