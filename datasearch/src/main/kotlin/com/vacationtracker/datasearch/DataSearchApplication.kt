package com.vacationtracker.datasearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
	scanBasePackages = ["com.vacationtracker.datasearch", "com.vacationtracker.database"]
)
@EnableJpaRepositories("com.vacationtracker.database.repository")
@EntityScan("com.vacationtracker.database.model")
class DataSearchApplication

fun main(args: Array<String>) {
	runApplication<DataSearchApplication>(*args)
}
