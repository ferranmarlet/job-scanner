package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.application.error.JobPostingNotFoundError
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice(assignableTypes = [GetJobPostingController::class, PatchJobPostingController::class])
class JobPostingExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "'${ex.value}' is not a valid job posting id; expected a UUID",
        ).apply {
            title = "Invalid job posting id"
            setProperty("parameter", ex.name)
        }

    @ExceptionHandler(JobPostingNotFoundError::class)
    fun handleNotFound(ex: JobPostingNotFoundError): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.message ?: "Job posting not found",
        ).apply {
            title = "Job posting not found"
        }
}
