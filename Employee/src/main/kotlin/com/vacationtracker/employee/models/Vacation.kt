package com.vacationtracker.employee.models

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table
data class Vacation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var startDate: LocalDate,

    var endDate: LocalDate,

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "employeeId", referencedColumnName = "id")
    var employee: Employee
) {
    override fun toString(): String {
        return "Vacation(id=$id, startDate=$startDate, endDate=$endDate)"
    }
}