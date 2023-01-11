package com.vacationtracker.database.dto

data class ExceptionDTO(
    val message: String,
    val status: Int,
    val statusText: String
)