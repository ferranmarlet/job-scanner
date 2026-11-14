package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.domain.jobposting.JobPosting
import org.ferran.jobscan.scanner.application.jobposting.GetJobPostingsUseCase
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingFilter
import org.ferran.jobscan.scanner.domain.jobposting.PagedJobPostingRequest
import org.ferran.jobscan.scanner.domain.shared.PagedResponse
import org.ferran.jobscan.scanner.domain.jobposting.PostingStatus
import org.ferran.jobscan.scanner.domain.jobposting.WorkMode
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
