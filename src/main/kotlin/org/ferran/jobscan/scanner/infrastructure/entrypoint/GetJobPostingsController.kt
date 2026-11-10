package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.application.GetJobPostingsUseCase
import org.ferran.jobscan.scanner.domain.JobPostingFilter
import org.ferran.jobscan.scanner.domain.PagedJobPostingRequest
import org.ferran.jobscan.scanner.domain.PagedResponse
import org.ferran.jobscan.scanner.domain.PostingStatus
import org.ferran.jobscan.scanner.domain.WorkMode
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/job-postings")
class GetJobPostingsController(private val getJobPostingsUseCase: GetJobPostingsUseCase) {

    @GetMapping
    fun get(
        @RequestParam externalSourceId: String?,
        @RequestParam workMode: WorkMode?,
        @RequestParam status: PostingStatus?,
        @RequestParam page: Int?
    ): PagedResponse<JobPosting> {
        val pagedJobPostingRequest = PagedJobPostingRequest(
            JobPostingFilter(
                externalSourceId, workMode, status,
            ),
            page ?: 0)

        return getJobPostingsUseCase.execute(pagedJobPostingRequest)
    }
}
