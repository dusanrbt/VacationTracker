package com.vacationtracker.database.dto

data class VacationDaysDTO(
    val totalDays: Int,
    val usedDays: Int,
    val availableDays: Int
)