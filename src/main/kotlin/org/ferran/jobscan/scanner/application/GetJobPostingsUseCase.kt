package org.ferran.jobscan.scanner.application

import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingRepository
import org.ferran.jobscan.scanner.domain.PagedJobPostingRequest
import org.ferran.jobscan.scanner.domain.PagedResponse
import org.springframework.stereotype.Service

@Service
class GetJobPostingsUseCase(
  private val jobPostingRepository: JobPostingRepository
) {
  fun execute(request: PagedJobPostingRequest): PagedResponse<JobPosting> =
    jobPostingRepository.findAll(request.filter, request.page)
}
