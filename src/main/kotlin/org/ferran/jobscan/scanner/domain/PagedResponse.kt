package org.ferran.jobscan.scanner.domain

data class PagedResponse<T> (
    val elements: List<T>,
    val page: Int,
    val totalPages: Int,
    val totalElements: Int,
)
