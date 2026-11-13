package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.domain.jobposting.JobPosting
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingId
import org.ferran.jobscan.scanner.application.GetJobPostingUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/job-postings")
class GetJobPostingController(private val getJobPosting: GetJobPostingUseCase) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): JobPosting =
        getJobPosting.execute(JobPostingId(id))
}
