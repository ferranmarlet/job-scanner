package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.application.jobposting.GetJobPostingUseCase
import org.ferran.jobscan.scanner.application.user.EnsureUserRegisteredCommand
import org.ferran.jobscan.scanner.application.user.EnsureUserRegisteredCommandHandler
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingMother
import org.ferran.jobscan.security.SecurityConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(GetJobPostingController::class)
@Import(SecurityConfig::class, UserRegistrationWebConfig::class)
class UserRegistrationInterceptorTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getJobPosting: GetJobPostingUseCase

    @MockitoBean
    private lateinit var ensureUserRegistered: EnsureUserRegisteredCommandHandler

    private val posting = JobPostingMother.create()

    @Test
    fun `registers the caller identified by the access token before handling the request`() {
        given(getJobPosting.execute(posting.id)).willReturn(posting)

        mockMvc.get("/job-postings/${posting.id.value}") {
            with(jwt().jwt { it.subject(SUB).tokenValue(ACCESS_TOKEN) })
        }.andExpect {
            status { isOk() }
        }

        verify(ensureUserRegistered).execute(EnsureUserRegisteredCommand(externalId = SUB, accessToken = ACCESS_TOKEN))
    }

    @Test
    fun `does not register anyone when the request has no access token`() {
        mockMvc.get("/job-postings/${posting.id.value}").andExpect {
            status { isUnauthorized() }
        }

        verifyNoInteractions(ensureUserRegistered)
    }

    private companion object {
        const val SUB = "6b8f2a4e-1c3d-4e5f-9a7b-0c1d2e3f4a5b"
        const val ACCESS_TOKEN = "access-token"
    }
}
