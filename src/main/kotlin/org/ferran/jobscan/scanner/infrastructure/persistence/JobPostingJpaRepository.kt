package org.ferran.jobscan.scanner.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JobPostingJpaRepository : JpaRepository<JobPostingEntity, UUID> {
  fun existsBySourceUrl(sourceUrl: String): Boolean

  fun findByExternalSourceIdAndExternalId(externalSourceId: String, externalId: String): JobPostingEntity?
}
