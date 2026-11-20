package org.ferran.jobscan.scanner.infrastructure.identity.cognito

import org.ferran.jobscan.scanner.domain.user.UserIdentityProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class CognitoConfig {

    @Bean
    fun cognitoUserIdentityProvider(properties: CognitoProperties): UserIdentityProvider =
        CognitoUserIdentityProvider(
            RestClient.builder()
                .baseUrl("https://${properties.loginDomain}")
                .build()
        )
}
