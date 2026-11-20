package org.ferran.jobscan.scanner.infrastructure.identity.cognito

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jobscan.cognito")
data class CognitoProperties(
    val loginDomain: String,
)
