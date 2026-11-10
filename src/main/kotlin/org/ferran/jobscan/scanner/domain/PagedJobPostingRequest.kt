package org.ferran.jobscan.scanner.domain

data class PagedJobPostingRequest (
    val filter: JobPostingFilter,
    val page: Int,
)
