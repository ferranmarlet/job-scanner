package org.ferran.jobscan.scanner.domain.jobposting

data class PagedJobPostingRequest (
    val filter: JobPostingFilter,
    val page: Int,
)
