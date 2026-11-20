package org.ferran.jobscan.scanner.infrastructure.identity.cognito

import org.ferran.jobscan.scanner.domain.user.UserEmailUnavailableError
import org.ferran.jobscan.scanner.domain.user.UserIdentityProvider
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class CognitoUserIdentityProvider(
    private val client: RestClient,
) : UserIdentityProvider {

    override fun findEmail(accessToken: String): String =
        client.get()
            .uri("/oauth2/userInfo")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .retrieve()
            .body<CognitoUserInfoResponse>()
            ?.email
            ?: throw UserEmailUnavailableError()
}
