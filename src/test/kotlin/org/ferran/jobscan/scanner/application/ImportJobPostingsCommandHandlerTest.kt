package org.ferran.jobscan.scanner.application

import net.datafaker.Faker
import org.ferran.jobscan.scanner.domain.ExternalJobSourceRepository
import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingRepository
import org.ferran.jobscan.scanner.domain.JobSearchCriteria
import org.ferran.jobscan.scanner.domain.WorkMode
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import java.net.URI
import java.util.concurrent.TimeUnit

@Tag("unit")
class ImportJobPostingsCommandHandlerTest {

    private val jobSourceRepository: ExternalJobSourceRepository = mock()
    private val jobPostingRepository: JobPostingRepository = mock()

    val faker = Faker()

    private val sut = ImportJobPostingsCommandHandler(jobSourceRepository, jobPostingRepository)

    @Test
    fun `should call JobSources with required parameters`() {
        val importJobPostingsCommand = anImportJobPostingsCommand()

        sut.execute(importJobPostingsCommand)

        verify(jobSourceRepository).findJobPostings(importJobPostingsCommand.criteria)
    }

    @Test
    fun `should save every job posting returned by JobSources`() {
        val importJobPostingsCommand = anImportJobPostingsCommand()
        val firstPosting = aJobPosting()
        val secondPosting = aJobPosting()
        `when`(jobSourceRepository.findJobPostings(importJobPostingsCommand.criteria))
            .thenReturn(listOf(firstPosting, secondPosting))

        sut.execute(importJobPostingsCommand)

        verify(jobPostingRepository).save(firstPosting)
        verify(jobPostingRepository).save(secondPosting)
        verifyNoMoreInteractions(jobPostingRepository)
    }

    private fun anImportJobPostingsCommand() =
        ImportJobPostingsCommand(
            JobSearchCriteria(
                listOf(
                    faker.lordOfTheRings().character(),
                    faker.lordOfTheRings().character(),
                ),
                faker.address().country(),
                faker.timeAndDate().past(30, TimeUnit.DAYS),
                WorkMode.REMOTE,
            )
        )

    private fun aJobPosting() =
        JobPosting(
            externalId = faker.internet().uuid(),
            externalSourceId = faker.company().name(),
            title = faker.job().title(),
            company = faker.company().name(),
            location = faker.address().country(),
            workMode = WorkMode.REMOTE,
            seniority = null,
            sourceUrl = URI.create("https://${faker.internet().domainName()}"),
        )
}
