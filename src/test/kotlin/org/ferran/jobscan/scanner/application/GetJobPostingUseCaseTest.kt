package org.ferran.jobscan.scanner.application

import org.ferran.jobscan.scanner.application.error.JobPostingNotFoundError
import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingId
import org.ferran.jobscan.scanner.domain.JobPostingRepository
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Tag("unit")
class GetJobPostingUseCaseTest {

  private val jobPostingRepository: JobPostingRepository = mock()

  private val sut = GetJobPostingUseCase(jobPostingRepository)

  private val id = JobPostingId.new()

  @Test
  fun `returns the job posting when it exists`() {
    val posting = JobPosting(
      id = id,
      externalId = "job-1",
      externalSourceId = "findwork",
      title = "Guild Navigator",
      company = "Spacing Guild",
      location = "Arrakis",
      workMode = null,
      seniority = null,
      sourceUrl = URI.create("https://example.com/jobs/1"),
    )
    `when`(jobPostingRepository.findById(id)).thenReturn(posting)

    assertEquals(posting, sut.execute(id))
  }

  @Test
  fun `throws JobPostingNotFoundError when the job posting does not exist`() {
    `when`(jobPostingRepository.findById(id)).thenReturn(null)

    val error = assertFailsWith<JobPostingNotFoundError> { sut.execute(id) }

    assertEquals("Job posting ${id.value} not found", error.message)
  }
}
