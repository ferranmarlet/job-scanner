package org.ferran.jobscan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class JobscanApplication

fun main(args: Array<String>) {
    runApplication<JobscanApplication>(*args)
}
