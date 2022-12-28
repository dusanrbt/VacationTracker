package com.vacationtracker.database.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*

@Entity
@Table
data class Vacation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var startDate: Date = Date(),

    var endDate: Date = Date(),

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    var employee: Employee = Employee()
)