package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.application.GetJobPostingUseCase
import org.ferran.jobscan.scanner.application.error.JobPostingNotFoundError
import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingId
import org.ferran.jobscan.scanner.domain.PostingStatus
import org.ferran.jobscan.scanner.domain.SalaryRange
import org.ferran.jobscan.scanner.domain.Seniority
import org.ferran.jobscan.scanner.domain.WorkMode
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
import java.net.URI
import java.time.Instant
import java.util.UUID

@WebMvcTest(GetJobPostingController::class)
@Import(SecurityConfig::class)
@WithMockUser
class GetJobPostingControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getJobPosting: GetJobPostingUseCase

    private val id = UUID.fromString("3f1c2a9e-6b4d-4c8e-9a1f-2d7e5b0c8a41")

    private val posting = JobPosting(
        id = JobPostingId(id),
        externalId = "job-1",
        externalSourceId = "findwork",
        title = "Guild Navigator",
        company = "Spacing Guild",
        location = "Arrakis",
        workMode = WorkMode.HYBRID,
        seniority = Seniority.SENIOR,
        salary = SalaryRange(min = 60_000, max = 80_000, currency = "EUR"),
        skills = setOf("prescience", "future sight"),
        sourceUrl = URI.create("https://example.com/jobs/1"),
        status = PostingStatus.OPEN,
        discoveredAt = Instant.parse("2026-10-30T09:15:00Z"),
    )

    @Test
    fun `returns the job posting as JSON`() {
        given(getJobPosting.execute(JobPostingId(id))).willReturn(posting)

        mockMvc.get("/job-postings/$id").andExpect {
            status { isOk() }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(id.toString()) }
            jsonPath("$.externalId") { value("job-1") }
            jsonPath("$.externalSourceId") { value("findwork") }
            jsonPath("$.title") { value("Guild Navigator") }
            jsonPath("$.company") { value("Spacing Guild") }
            jsonPath("$.location") { value("Arrakis") }
            jsonPath("$.workMode") { value("HYBRID") }
            jsonPath("$.seniority") { value("SENIOR") }
            jsonPath("$.salary.min") { value(60_000) }
            jsonPath("$.salary.max") { value(80_000) }
            jsonPath("$.salary.currency") { value("EUR") }
            jsonPath("$.skills.length()") { value(2) }
            jsonPath("$.sourceUrl") { value("https://example.com/jobs/1") }
            jsonPath("$.status") { value("OPEN") }
            jsonPath("$.discoveredAt") { value("2026-10-30T09:15:00Z") }
        }
    }

    @Test
    fun `passes the path id to the use case`() {
        given(getJobPosting.execute(JobPostingId(id))).willReturn(posting)

        mockMvc.get("/job-postings/$id")

        verify(getJobPosting).execute(JobPostingId(id))
    }

    @Test
    fun `returns 404 problem detail when the job posting does not exist`() {
        given(getJobPosting.execute(JobPostingId(id))).willThrow(JobPostingNotFoundError(JobPostingId(id)))

        mockMvc.get("/job-postings/$id").andExpect {
            status { isNotFound() }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.status") { value(404) }
            jsonPath("$.title") { value("Job posting not found") }
            jsonPath("$.detail") { value("Job posting $id not found") }
        }
    }

    @Test
    fun `returns 400 problem detail when the id is not a UUID`() {
        mockMvc.get("/job-postings/not-a-uuid").andExpect {
            status { isBadRequest() }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.status") { value(400) }
            jsonPath("$.title") { value("Invalid job posting id") }
            jsonPath("$.detail") { value("'not-a-uuid' is not a valid job posting id; expected a UUID") }
            jsonPath("$.parameter") { value("id") }
        }

        verifyNoInteractions(getJobPosting)
    }
}
