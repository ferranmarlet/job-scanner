package org.ferran.jobscan.scanner.application.error

import org.ferran.jobscan.scanner.domain.JobPostingId

class JobPostingNotFoundError(id: JobPostingId) : RuntimeException("Job posting ${id.value} not found")
