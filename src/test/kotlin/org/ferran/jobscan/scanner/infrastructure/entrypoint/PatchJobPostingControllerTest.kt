package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.application.jobposting.UpdateJobPostingCommand
import org.ferran.jobscan.scanner.application.jobposting.UpdateJobPostingCommandHandler
import org.ferran.jobscan.scanner.application.jobposting.JobPostingNotFoundError
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingId
import org.ferran.jobscan.scanner.domain.jobposting.PostingStatus
import org.ferran.jobscan.security.SecurityConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import tools.jackson.databind.json.JsonMapper

@WebMvcTest(PatchJobPostingController::class)
@Import(SecurityConfig::class)
@WithMockUser
class PatchJobPostingControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var updateJobPostingCommandHandler: UpdateJobPostingCommandHandler

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    val id = JobPostingId.new()

    @Test
    fun `returns 404 problem detail when the job posting does not exist`() {
        val request = PatchJobPostingRequest(PostingStatus.entries.random())
        given(updateJobPostingCommandHandler.execute(UpdateJobPostingCommand(id, request.status))).willThrow(JobPostingNotFoundError(id))

        mockMvc.patch("/job-postings/${id.value}"){
            contentType = MediaType.APPLICATION_JSON
            content = jsonMapper.writeValueAsString(request)
        }.andExpect {
            status { isNotFound() }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.status") { value(404) }
            jsonPath("$.title") { value("Job posting not found") }
            jsonPath("$.detail") { value("Job posting ${id.value} not found") }
        }
    }

    @Test
    fun `returns 400 status code when the id is not a UUID`() {
        mockMvc.patch("/job-postings/not-a-uuid").andExpect {
            status { isBadRequest() }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.status") { value(400) }
            jsonPath("$.title") { value("Invalid job posting id") }
            jsonPath("$.detail") { value("'not-a-uuid' is not a valid job posting id; expected a UUID") }
            jsonPath("$.parameter") { value("id") }
        }

        verifyNoInteractions(updateJobPostingCommandHandler)
    }

    @Test
    fun `passes the command to the handler`() {
        val request = PatchJobPostingRequest(PostingStatus.entries.random())
        mockMvc.patch("/job-postings/${id.value}"){
            contentType = MediaType.APPLICATION_JSON
            content = jsonMapper.writeValueAsString(request)
        }.andExpect {
            status { is2xxSuccessful() }
        }

        val command = UpdateJobPostingCommand(
            id,
            request.status
        )
        verify(updateJobPostingCommandHandler).execute(command)
    }
}
