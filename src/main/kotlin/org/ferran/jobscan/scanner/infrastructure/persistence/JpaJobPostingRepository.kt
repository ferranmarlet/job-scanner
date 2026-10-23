package org.ferran.jobscan.scanner.infrastructure.persistence

import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingId
import org.ferran.jobscan.scanner.domain.JobPostingRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Component
class JpaJobPostingRepository(
  private val jpaRepository: JobPostingJpaRepository,
) : JobPostingRepository {

  override fun save(posting: JobPosting) {
    jpaRepository.save(JobPostingEntityMapper.toEntity(posting))
  }

  // Transactional because mapping reads the lazy skills collection
  @Transactional(readOnly = true)
  override fun findById(id: JobPostingId): JobPosting? =
    jpaRepository.findByIdOrNull(id.value)?.let(JobPostingEntityMapper::toDomain)

  override fun existsBySourceUrl(url: URI): Boolean =
    jpaRepository.existsBySourceUrl(url.toString())
}
