package org.ferran.jobscan.scanner.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jobscan.scanner")
data class ScannerProperties(
    val findworkApiKey: String,
    val findworkBaseUrl: String,
)
