package org.ferran.jobscan.scanner.infrastructure.persistence

import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingId
import org.ferran.jobscan.scanner.domain.SalaryRange
import java.net.URI

object JobPostingEntityMapper {

  fun toEntity(posting: JobPosting): JobPostingEntity =
    JobPostingEntity(
      id = posting.id.value,
      externalId = posting.externalId,
      externalSourceId = posting.externalSourceId,
      title = posting.title,
      company = posting.company,
      location = posting.location,
      workMode = posting.workMode,
      seniority = posting.seniority,
      salary = posting.salary?.let { SalaryEmbeddable(it.min, it.max, it.currency) },
      skills = posting.skills.toMutableSet(),
      sourceUrl = posting.sourceUrl.toString(),
      status = posting.status,
      discoveredAt = posting.discoveredAt,
    )

  fun toDomain(entity: JobPostingEntity): JobPosting =
    JobPosting(
      id = JobPostingId(entity.id),
      externalId = entity.externalId,
      externalSourceId = entity.externalSourceId,
      title = entity.title,
      company = entity.company,
      location = entity.location,
      workMode = entity.workMode,
      seniority = entity.seniority,
      salary = entity.salary?.let { SalaryRange(it.min, it.max, it.currency) },
      skills = entity.skills.toSet(),
      sourceUrl = URI.create(entity.sourceUrl),
      status = entity.status,
      discoveredAt = entity.discoveredAt,
    )
}
