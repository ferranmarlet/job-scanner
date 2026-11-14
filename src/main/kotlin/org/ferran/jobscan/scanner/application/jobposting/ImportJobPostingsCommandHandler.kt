package org.ferran.jobscan.scanner.application.jobposting

import org.ferran.jobscan.scanner.domain.jobposting.ExternalJobSourceRepository
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingRepository
import org.ferran.jobscan.scanner.domain.jobposting.JobSearchCriteria
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
