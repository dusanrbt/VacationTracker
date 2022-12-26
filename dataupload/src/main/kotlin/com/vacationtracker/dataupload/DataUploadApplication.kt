package com.vacationtracker.dataupload

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
	scanBasePackages = ["com.vacationtracker.dataupload", "com.vacationtracker.database"]
)
@EnableJpaRepositories("com.vacationtracker.database.repository")
@EntityScan("com.vacationtracker.database.model")
class DataUploadApplication

fun main(args: Array<String>) {
	runApplication<DataUploadApplication>(*args)
}
