package com.vacationtracker.admin.models

import javax.persistence.*

@Entity
@Table
data class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var email: String,

    var password: String,

    @OneToMany(mappedBy = "id")
    var vacations: List<Vacation> = arrayListOf(),

    @ElementCollection
    @CollectionTable(
        name = "employeeTotalVacationDays",
        joinColumns = [JoinColumn(name = "employeeId", referencedColumnName = "id")]
    )
    @MapKeyColumn(name = "year")
    @Column(name = "vacationDays")
    var totalVacationDays: MutableMap<Int, Int> = HashMap()
)