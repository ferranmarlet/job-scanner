package org.ferran.jobscan.scanner.application.jobposting

import org.ferran.jobscan.scanner.domain.jobposting.JobPosting
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingRepository
import org.ferran.jobscan.scanner.domain.jobposting.PagedJobPostingRequest
import org.ferran.jobscan.scanner.domain.shared.PagedResponse
import org.springframework.stereotype.Service

@Service
class GetJobPostingsUseCase(
    private val jobPostingRepository: JobPostingRepository
) {
    fun execute(request: PagedJobPostingRequest): PagedResponse<JobPosting> =
        jobPostingRepository.findAll(request.filter, request.page)
}
