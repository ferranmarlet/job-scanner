package org.ferran.jobscan.scanner.application

import org.ferran.jobscan.scanner.domain.ExternalJobSourceRepository
import org.ferran.jobscan.scanner.domain.JobSearchCriteria
import org.springframework.stereotype.Service

data class ImportJobPostingsCommand (
  val criteria: JobSearchCriteria,
)

@Service
class ImportJobPostingsCommandHandler(
  private val jobPostingsRepository: ExternalJobSourceRepository
) {

  fun execute(command: ImportJobPostingsCommand) {
    val jobPostings = this.jobPostingsRepository.findJobPostings(command.criteria)

    // no database to store jobPostings yet, so we just print the results
    jobPostings.forEach(::println)
  }
}
