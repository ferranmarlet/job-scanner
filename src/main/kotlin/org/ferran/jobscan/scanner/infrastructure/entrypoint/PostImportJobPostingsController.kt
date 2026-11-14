package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.application.jobposting.ImportJobPostingsCommand
import org.ferran.jobscan.scanner.application.jobposting.ImportJobPostingsCommandHandler
import org.ferran.jobscan.scanner.domain.jobposting.JobSearchCriteria
import org.ferran.jobscan.scanner.domain.jobposting.WorkMode
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

data class ImportJobPostingsRequest (
    val keywords: List<String>,
    val location: String?,
    val postedAfter: Instant?,
    val workMode: WorkMode?,
)

@RestController
@RequestMapping("/import-job-postings")
class PostImportJobPostingsController(private val importJobPostingsCommandHandler: ImportJobPostingsCommandHandler) {

    @PostMapping
    fun post(@RequestBody request: ImportJobPostingsRequest) {
        val importJobPostingsCommand = ImportJobPostingsCommand(
            JobSearchCriteria(
                request.keywords,
                request.location,
                request.postedAfter,
                request.workMode,
            )
        )
        importJobPostingsCommandHandler.execute(importJobPostingsCommand)
    }
}
