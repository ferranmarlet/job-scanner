package org.ferran.jobscan.scanner.infrastructure.jobsource.findwork

import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.Seniority
import org.ferran.jobscan.scanner.domain.WorkMode
import java.net.URI

object FindworkJobMapper {

  fun toDomain(dto: FindworkJobDto): JobPosting? {
    val title = dto.role?.takeIf { it.isNotBlank() } ?: return null
    val company = dto.companyName?.takeIf { it.isNotBlank() } ?: return null
    val url = dto.url?.takeIf { it.isNotBlank() } ?: return null

    return JobPosting(
      title = title,
      company = company,
      location = dto.location.orEmpty(),
      workMode = if (dto.remote) WorkMode.REMOTE else WorkMode.ONSITE,
      seniority = null,
      skills = dto.keywords.toSet(),
      sourceUrl = URI.create(url),
    )
  }
}
