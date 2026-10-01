package org.ferran.jobscan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JobscanApplication

fun main(args: Array<String>) {
	runApplication<JobscanApplication>(*args)
}
