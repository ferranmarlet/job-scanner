package org.ferran.jobscan.scanner.application.jobposting

import org.ferran.jobscan.scanner.domain.jobposting.JobPosting
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingId
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingRepository
import org.springframework.stereotype.Service

@Service
class GetJobPostingUseCase(
    private val jobPostingRepository: JobPostingRepository
) {
    fun execute(id: JobPostingId): JobPosting =
        jobPostingRepository.findById(id) ?: throw JobPostingNotFoundError(id)
}
