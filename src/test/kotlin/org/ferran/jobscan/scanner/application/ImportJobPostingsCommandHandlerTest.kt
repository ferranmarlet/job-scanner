package org.ferran.jobscan.scanner.application

import net.datafaker.Faker
import org.ferran.jobscan.scanner.domain.ExternalJobSourceRepository
import org.ferran.jobscan.scanner.domain.JobSearchCriteria
import org.ferran.jobscan.scanner.domain.WorkMode
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.concurrent.TimeUnit

@Tag("unit")
class ImportJobPostingsCommandHandlerTest {

  private val jobSourceRepository: ExternalJobSourceRepository = mock()

  val faker = Faker()

  @Test
  fun `should call JobSources with required parameters`() {
    val sut = ImportJobPostingsCommandHandler(jobSourceRepository)
    val importJobPostingsCommand = ImportJobPostingsCommand(
        JobSearchCriteria(
          listOf(
            faker.lordOfTheRings().character(),
            faker.lordOfTheRings().character(),
          ),
          faker.address().country(),
          faker.bool().bool(),
          faker.timeAndDate().past(30, TimeUnit.DAYS),
            WorkMode.REMOTE
          )
        )
    sut.execute(importJobPostingsCommand)

    verify(jobSourceRepository).findJobPostings(importJobPostingsCommand.criteria)
  }
}
