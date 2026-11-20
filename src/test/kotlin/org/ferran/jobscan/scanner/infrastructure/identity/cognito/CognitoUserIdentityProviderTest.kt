package org.ferran.jobscan.scanner.infrastructure.identity.cognito

import org.ferran.jobscan.scanner.domain.user.UserEmailUnavailableError
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Tag("unit")
class CognitoUserIdentityProviderTest {

    private val restClientBuilder = RestClient.builder().baseUrl(COGNITO_DOMAIN)
    private val cognito = MockRestServiceServer.bindTo(restClientBuilder).build()

    private val sut = CognitoUserIdentityProvider(restClientBuilder.build())

    @Test
    fun `returns the email from Cognito's userInfo endpoint for the access token`() {
        cognito.expect(requestTo("$COGNITO_DOMAIN/oauth2/userInfo"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer $ACCESS_TOKEN"))
            .andRespond(
                withSuccess(
                    """{"sub":"$SUB","email":"$EMAIL","email_verified":"true","username":"$SUB"}""",
                    MediaType.APPLICATION_JSON,
                )
            )

        assertEquals(EMAIL, sut.findEmail(ACCESS_TOKEN))
        cognito.verify()
    }

    @Test
    fun `throws UserEmailUnavailableError when Cognito's userInfo response has no email`() {
        cognito.expect(requestTo("$COGNITO_DOMAIN/oauth2/userInfo"))
            .andRespond(withSuccess("""{"sub":"$SUB","username":"$SUB"}""", MediaType.APPLICATION_JSON))

        assertFailsWith<UserEmailUnavailableError> { sut.findEmail(ACCESS_TOKEN) }
    }

    private companion object {
        const val COGNITO_DOMAIN = "https://jobscan.auth.eu-west-1.amazoncognito.com"
        const val ACCESS_TOKEN = "access-token"
        const val SUB = "6b8f2a4e-1c3d-4e5f-9a7b-0c1d2e3f4a5b"
        const val EMAIL = "paul.atreides@example.com"
    }
}
