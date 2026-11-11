package org.ferran.jobscan.scanner.application

import org.ferran.jobscan.scanner.application.error.JobPostingNotFoundError
import org.ferran.jobscan.scanner.domain.InvalidStatusTransitionError
import org.ferran.jobscan.scanner.domain.JobPostingId
import org.ferran.jobscan.scanner.domain.JobPostingRepository
import org.ferran.jobscan.scanner.domain.PostingStatus
import org.springframework.stereotype.Service

data class UpdateJobPostingCommand (
    val id: JobPostingId,
    val status: PostingStatus?,
)

@Service
class UpdateJobPostingCommandHandler(
    private val jobPostingRepository: JobPostingRepository,
) {

    @Throws(JobPostingNotFoundError::class, InvalidStatusTransitionError::class)
    fun execute(command: UpdateJobPostingCommand) {

        val jobPosting = jobPostingRepository.findById(command.id) ?: throw JobPostingNotFoundError(command.id)
        val updatedJobPosting = command.status?.let { jobPosting.changeStatus(it) } ?: jobPosting

        jobPostingRepository.save(updatedJobPosting)
    }
}
