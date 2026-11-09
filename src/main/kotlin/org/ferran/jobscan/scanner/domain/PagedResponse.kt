package org.ferran.jobscan.scanner.domain

data class PagedResponse<T> (
  val items: List<T>,
  val page: Int,
  val totalPages: Int,
  val totalItems: Int,
)
