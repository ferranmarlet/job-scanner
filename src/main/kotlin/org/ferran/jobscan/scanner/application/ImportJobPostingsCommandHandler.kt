package org.ferran.jobscan.scanner.application

import org.ferran.jobscan.scanner.domain.ExternalJobSourceRepository
import org.ferran.jobscan.scanner.domain.JobPostingRepository
import org.ferran.jobscan.scanner.domain.JobSearchCriteria
import org.springframework.stereotype.Service

data class ImportJobPostingsCommand (
  val criteria: JobSearchCriteria,
)

@Service
class ImportJobPostingsCommandHandler(
  private val externalJobSource: ExternalJobSourceRepository,
  private val jobRepository: JobPostingRepository,
) {

  fun execute(command: ImportJobPostingsCommand) {
    val jobSourceResponse = this.externalJobSource.findJobPostings(command.criteria)

    jobSourceResponse.forEach(this.jobRepository::save)
  }
}
