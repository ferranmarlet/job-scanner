package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.application.jobposting.UpdateJobPostingCommand
import org.ferran.jobscan.scanner.application.jobposting.UpdateJobPostingCommandHandler
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingId
import org.ferran.jobscan.scanner.domain.jobposting.PostingStatus
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

data class PatchJobPostingRequest (
    val status: PostingStatus?,
)

@RestController
@RequestMapping("/job-postings")
class PatchJobPostingController(private val handler: UpdateJobPostingCommandHandler) {

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: UUID, @RequestBody request: PatchJobPostingRequest) {
        handler.execute(
            UpdateJobPostingCommand(
                JobPostingId(id),
                request.status
            )
        )
    }
}
