package org.ferran.jobscan.scanner.domain

import java.time.Instant

data class JobSearchCriteria(
    val keywords: List<String> = emptyList(),
    val location: String? = null,
    val remote: Boolean? = null,
    val postedAfter: Instant? = null,
    val workMode: WorkMode? = null,
)


interface ExternalJobSourceRepository {
  fun findJobPostings(criteria: JobSearchCriteria): List<JobPosting>
}
