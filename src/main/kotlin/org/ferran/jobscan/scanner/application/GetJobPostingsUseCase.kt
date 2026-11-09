package org.ferran.jobscan.scanner.application

import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingFilter
import org.ferran.jobscan.scanner.domain.JobPostingRepository
import org.ferran.jobscan.scanner.domain.PostingStatus
import org.ferran.jobscan.scanner.domain.WorkMode
import org.springframework.stereotype.Service

@Service
class GetJobPostingsUseCase(
  private val jobPostingRepository: JobPostingRepository
) {
  fun execute(externalSourceId: String?, workMode: WorkMode?, status: PostingStatus?): List<JobPosting> =
    jobPostingRepository.findAll(JobPostingFilter(externalSourceId, workMode, status))
}
