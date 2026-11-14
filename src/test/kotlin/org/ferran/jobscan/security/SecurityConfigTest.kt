package org.ferran.jobscan.security

import org.ferran.jobscan.scanner.application.jobposting.GetJobPostingUseCase
import org.ferran.jobscan.scanner.application.jobposting.ImportJobPostingsCommandHandler
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingMother
import org.ferran.jobscan.scanner.infrastructure.entrypoint.GetJobPostingController
import org.ferran.jobscan.scanner.infrastructure.entrypoint.PostImportJobPostingsController
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@WebMvcTest(GetJobPostingController::class, PostImportJobPostingsController::class)
@Import(SecurityConfig::class)
class SecurityConfigTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtAuthenticationConverter: JwtAuthenticationConverter

    @MockitoBean
    private lateinit var getJobPosting: GetJobPostingUseCase

    @MockitoBean
    private lateinit var importJobPostingsCommandHandler: ImportJobPostingsCommandHandler

    private val posting = JobPostingMother.create()

    private val importBody = """{"keywords":["kotlin"]}"""

    @Test
    fun `returns 401 when the request has no token`() {
        mockMvc.get("/job-postings/${posting.id.value}").andExpect {
            status { isUnauthorized() }
        }

        verifyNoInteractions(getJobPosting)
    }

    @Test
    fun `lets an authenticated user read job postings`() {
        given(getJobPosting.execute(posting.id)).willReturn(posting)

        mockMvc.get("/job-postings/${posting.id.value}") {
            with(jwt())
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `returns 403 when a user outside the admin group imports job postings`() {
        mockMvc.post("/import-job-postings") {
            with(jwt())
            contentType = MediaType.APPLICATION_JSON
            content = importBody
        }.andExpect {
            status { isForbidden() }
        }

        verifyNoInteractions(importJobPostingsCommandHandler)
    }

    @Test
    fun `lets an admin import job postings`() {
        mockMvc.post("/import-job-postings") {
            with(jwt().authorities(SimpleGrantedAuthority("ROLE_${SecurityConfig.ADMIN_ROLE}")))
            contentType = MediaType.APPLICATION_JSON
            content = importBody
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `maps Cognito groups to roles`() {
        val token = accessToken { it.claim("cognito:groups", listOf("admin", "viewer")) }

        val roles = jwtAuthenticationConverter.convert(token)!!.authorities
            .mapNotNull { it.authority }
            .filter { it.startsWith("ROLE_") } // Spring Security also adds FACTOR_BEARER

        assertEquals(setOf("ROLE_admin", "ROLE_viewer"), roles.toSet())
    }

    @Test
    fun `accepts an access token issued to the configured client`() {
        val token = accessToken()

        assertFalse(validators.any { it.validate(token).hasErrors() })
    }

    @Test
    fun `rejects a token issued to another client`() {
        val token = accessToken { it.claim("client_id", "another-client") }

        assertTrue(config.clientIdValidator(properties).validate(token).hasErrors())
    }

    @Test
    fun `rejects an ID token`() {
        val token = accessToken { it.claim("token_use", "id") }

        assertTrue(config.accessTokenValidator().validate(token).hasErrors())
    }

    private val config = SecurityConfig()
    private val properties = SecurityProperties(cognitoClientId = CLIENT_ID)
    private val validators = listOf(config.clientIdValidator(properties), config.accessTokenValidator())

    private fun accessToken(customize: (Jwt.Builder) -> Unit = {}): Jwt =
        Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .subject("user")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .claim("client_id", CLIENT_ID)
            .claim("token_use", "access")
            .apply(customize)
            .build()

    companion object {
        private const val CLIENT_ID = "spa-client"
    }
}
