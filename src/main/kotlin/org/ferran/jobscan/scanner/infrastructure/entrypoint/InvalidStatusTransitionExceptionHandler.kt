package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.domain.InvalidStatusTransitionError
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [PatchJobPostingController::class])
class InvalidStatusTransitionExceptionHandler {
    @ExceptionHandler(InvalidStatusTransitionError::class)
    fun handleInvalidStatusTransition(ex: InvalidStatusTransitionError): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.message ?: "Invalid status transition",
        ).apply {
            title = "Invalid status transition"
            setProperty("from", ex.from)
            setProperty("to", ex.to)
        }
}
