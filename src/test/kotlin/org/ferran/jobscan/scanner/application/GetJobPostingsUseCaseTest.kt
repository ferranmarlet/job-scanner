package org.ferran.jobscan.scanner.application

import net.datafaker.Faker
import org.ferran.jobscan.scanner.domain.jobposting.JobPosting
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingFilter
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingMother
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingRepository
import org.ferran.jobscan.scanner.domain.jobposting.PagedJobPostingRequest
import org.ferran.jobscan.scanner.domain.shared.PagedResponse
import org.ferran.jobscan.scanner.domain.jobposting.PostingStatus
import org.ferran.jobscan.scanner.domain.jobposting.WorkMode
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals

@Tag("unit")
class GetJobPostingsUseCaseTest {

    private val jobPostingRepository: JobPostingRepository = mock()

    private val sut = GetJobPostingsUseCase(jobPostingRepository)

    val faker = Faker()

    @Test
    fun `returns an empty list when there are no JobPostings`() {
        `when`(jobPostingRepository.findAll(
            JobPostingFilter(),
            0)).thenReturn(PagedResponse<JobPosting>(listOf(), 0, 0, 0))

        assertEquals(0, sut.execute(PagedJobPostingRequest(JobPostingFilter(), 0)).elements.size)
    }

    @Test
    fun `returns JobPostings`() {

        val expected = PagedResponse<JobPosting>(listOf(
            JobPostingMother.create(),
            JobPostingMother.create(),
        ),
            0,
            1,
            2,
        )
        `when`(jobPostingRepository.findAll(JobPostingFilter(), 0)).thenReturn(expected)

        assertEquals(expected, sut.execute(PagedJobPostingRequest(JobPostingFilter(),0)))
    }

    @Test
    fun `creates filter and passes it to the repository`() {

        val filter = JobPostingFilter(
            faker.company().name(),
            WorkMode.entries.random(),
            PostingStatus.entries.random(),
        )

        sut.execute(PagedJobPostingRequest(filter,0))
        verify(jobPostingRepository).findAll(filter, 0)
    }
}
