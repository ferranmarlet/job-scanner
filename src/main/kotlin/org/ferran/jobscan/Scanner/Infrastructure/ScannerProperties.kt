package org.ferran.jobscan.scanner.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI
import java.time.Duration

@ConfigurationProperties("jobscan.scanner")
data class ScannerProperties(
	val findWorksApiKey: String,
)
