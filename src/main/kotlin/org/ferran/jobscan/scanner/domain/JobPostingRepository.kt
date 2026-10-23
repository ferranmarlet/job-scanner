package org.ferran.jobscan.scanner.domain

import java.net.URI

interface JobPostingRepository {
  fun save(posting: JobPosting)

  fun findById(id: JobPostingId): JobPosting?

  fun existsBySourceUrl(url: URI): Boolean
}
