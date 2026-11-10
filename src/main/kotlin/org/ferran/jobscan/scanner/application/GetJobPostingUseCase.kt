package org.ferran.jobscan.scanner.application

import org.ferran.jobscan.scanner.application.error.JobPostingNotFoundError
import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingId
import org.ferran.jobscan.scanner.domain.JobPostingRepository
import org.springframework.stereotype.Service

@Service
class GetJobPostingUseCase(
    private val jobPostingRepository: JobPostingRepository
) {
    fun execute(id: JobPostingId): JobPosting =
        jobPostingRepository.findById(id) ?: throw JobPostingNotFoundError(id)
}
