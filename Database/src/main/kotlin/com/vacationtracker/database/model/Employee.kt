package com.vacationtracker.database.model

import jakarta.persistence.*

@Entity
@Table
data class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true)
    var email: String = "",

    var password: String = "",

    @OneToMany(mappedBy = "id")
    var vacations: List<Vacation> = arrayListOf(),

    @ElementCollection
    @CollectionTable(
        name = "employee_total_vacation_days",
        joinColumns = [JoinColumn(name = "employee_id", referencedColumnName = "id")]
    )
    @MapKeyColumn(name = "year")
    @Column(name = "vacation_days")
    var totalVacationDays: MutableMap<Int, Int> = HashMap()
)