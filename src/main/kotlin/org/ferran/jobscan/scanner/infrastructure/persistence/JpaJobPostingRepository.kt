package org.ferran.jobscan.scanner.infrastructure.persistence

import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingFilter
import org.ferran.jobscan.scanner.domain.JobPostingId
import org.ferran.jobscan.scanner.domain.JobPostingRepository
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Component
class JpaJobPostingRepository(
  private val jpaRepository: JobPostingEntityRepository,
) : JobPostingRepository {

  @Transactional
  override fun save(posting: JobPosting) {
    jpaRepository.save(JobPostingEntityMapper.toEntity(withExistingIdentity(posting)))
  }

  // A posting imported again arrives with a new id and default lifecycle values, so it takes
  // over those of the stored posting it updates.
  private fun withExistingIdentity(posting: JobPosting): JobPosting {
    val existing = posting.externalId
      ?.let { jpaRepository.findByExternalSourceIdAndExternalId(posting.externalSourceId, it) }
      ?: return posting

    if (existing.id == posting.id.value) return posting

    return posting.copy(
      id = JobPostingId(existing.id),
      status = existing.status,
      discoveredAt = existing.discoveredAt,
    )
  }

  // Transactional because mapping reads the lazy skills collection
  @Transactional(readOnly = true)
  override fun findById(id: JobPostingId): JobPosting? =
    jpaRepository.findByIdOrNull(id.value)?.let(JobPostingEntityMapper::toDomain)

  override fun existsBySourceUrl(url: URI): Boolean =
    jpaRepository.existsBySourceUrl(url.toString())

  @Transactional(readOnly = true)
  override fun findAll(filter: JobPostingFilter): List<JobPosting> {
    val spec = Specification.allOf(
      JobPostingSpecs.hasExternalSourceId(filter.externalSourceId),
      JobPostingSpecs.hasWorkMode(filter.workMode),
      JobPostingSpecs.hasStatus(filter.status),
    )

    return jpaRepository.findAll(spec).map(JobPostingEntityMapper::toDomain)
  }
}
