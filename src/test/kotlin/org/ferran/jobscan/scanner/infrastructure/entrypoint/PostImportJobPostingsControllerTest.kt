package org.ferran.jobscan.scanner.infrastructure.entrypoint

import net.datafaker.Faker
import org.ferran.jobscan.scanner.application.GetJobPostingUseCase
import org.ferran.jobscan.scanner.application.ImportJobPostingsCommand
import org.ferran.jobscan.scanner.application.ImportJobPostingsCommandHandler
import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingId
import org.ferran.jobscan.scanner.domain.JobSearchCriteria
import org.ferran.jobscan.scanner.domain.PostingStatus
import org.ferran.jobscan.scanner.domain.SalaryRange
import org.ferran.jobscan.scanner.domain.Seniority
import org.ferran.jobscan.scanner.domain.WorkMode
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import tools.jackson.databind.json.JsonMapper
import java.net.URI
import java.time.Instant
import java.util.UUID
import kotlin.math.exp

@WebMvcTest(PostImportJobPostingsController::class)
class PostImportJobPostingsControllerTest {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var jsonMapper: JsonMapper

  @MockitoBean
  private lateinit var importJobPostingsCommandHandler: ImportJobPostingsCommandHandler

  val faker = Faker()

  @Test
  fun `constructs command and calls handler`() {
    val request = ImportJobPostingsRequest(
      listOf(
        faker.starWars().droids(),
        faker.finalFantasyXIV().job()
      ),
      faker.address().city(),
      faker.timeAndDate().past(),
      WorkMode.entries.random(),
    )

    val expectedCommand = ImportJobPostingsCommand(
        JobSearchCriteria(
          request.keywords,
          request.location,
          request.postedAfter,
          request.workMode,
        )
      )

    mockMvc.post("/import-job-postings") {
      contentType = MediaType.APPLICATION_JSON
      content = jsonMapper.writeValueAsString(request)
    }.andExpect {
      status { isOk() }
    }

    verify(importJobPostingsCommandHandler).execute(
      expectedCommand
    )
  }

  @Test
  fun `returns 400 problem detail when the request is invalid`() {
    mockMvc.post("/import-job-postings").andExpect {
      status { isBadRequest() }
      content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
      jsonPath("$.status") { value(400) }
      jsonPath("$.title") { value("Invalid job posting id") }
      jsonPath("$.detail") { value("'not-a-uuid' is not a valid job posting id; expected a UUID") }
      jsonPath("$.parameter") { value("id") }
    }

    verifyNoInteractions(importJobPostingsCommandHandler)
  }
}
