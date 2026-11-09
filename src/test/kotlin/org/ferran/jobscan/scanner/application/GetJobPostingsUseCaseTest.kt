package org.ferran.jobscan.scanner.application

import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingFilter
import org.ferran.jobscan.scanner.domain.JobPostingId
import org.ferran.jobscan.scanner.domain.JobPostingRepository
import org.ferran.jobscan.scanner.domain.Seniority
import org.ferran.jobscan.scanner.domain.WorkMode
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.net.URI
import kotlin.test.assertEquals

@Tag("unit")
class GetJobPostingsUseCaseTest {

  private val jobPostingRepository: JobPostingRepository = mock()

  private val sut = GetJobPostingsUseCase(jobPostingRepository)

  @Test
  fun `returns an empty list when there are no JobPostings`() {
    `when`(jobPostingRepository.findAll(JobPostingFilter())).thenReturn(listOf())

    assert(sut.execute(null, null, null).isEmpty())
  }

  @Test
  fun `returns JobPostings`() {

    val expected: List<JobPosting> = listOf(
      aJobPosting(),
      aJobPosting()
    )
    `when`(jobPostingRepository.findAll(JobPostingFilter())).thenReturn(expected)

    assertEquals(expected, sut.execute(null, null, null))
  }

  private fun aJobPosting() =
    JobPosting(
      id = JobPostingId.new(),
      externalId = null,
      externalSourceId = "unknown",
      title = "Untitled",
      company = "Unknown",
      location = "",
      workMode = WorkMode.ONSITE,
      seniority = Seniority.MID,
      sourceUrl = URI.create("about:blank"),
    )
}
