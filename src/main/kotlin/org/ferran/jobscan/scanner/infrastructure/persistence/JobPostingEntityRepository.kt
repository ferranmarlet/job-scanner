package org.ferran.jobscan.scanner.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.UUID

interface JobPostingEntityRepository : JpaRepository<JobPostingEntity, UUID>,
    JpaSpecificationExecutor<JobPostingEntity> {

    fun existsBySourceUrl(sourceUrl: String): Boolean

    fun findByExternalSourceIdAndExternalId(externalSourceId: String, externalId: String): JobPostingEntity?
}
