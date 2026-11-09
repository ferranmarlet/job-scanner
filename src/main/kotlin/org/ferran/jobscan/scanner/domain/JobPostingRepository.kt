package org.ferran.jobscan.scanner.domain

import java.net.URI

data class JobPostingFilter(
  val externalSourceId: String? = null,
  val workMode: WorkMode? = null,
  val status: PostingStatus? = null,
)

interface JobPostingRepository {

  fun save(posting: JobPosting)

  fun findById(id: JobPostingId): JobPosting?

  fun existsBySourceUrl(url: URI): Boolean

  fun findAll(filter: JobPostingFilter): List<JobPosting>
}
