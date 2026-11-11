package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.application.error.JobPostingNotFoundError
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [GetJobPostingController::class, PatchJobPostingController::class])
class JobPostingNotFoundExceptionHandler {
    @ExceptionHandler(JobPostingNotFoundError::class)
    fun handleNotFound(ex: JobPostingNotFoundError): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.message ?: "Job posting not found",
        ).apply {
            title = "Job posting not found"
        }
}
