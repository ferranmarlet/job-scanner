package org.ferran.jobscan.scanner.application

import org.ferran.jobscan.scanner.JobPosting
import org.ferran.jobscan.scanner.JobPostingId
import org.ferran.jobscan.scanner.Seniority
import org.ferran.jobscan.scanner.WorkMode
import org.springframework.stereotype.Service
import java.net.URI

@Service
class GetJobPostingUseCase {
  // TODO: load from a repository once one exists
  fun execute(id: JobPostingId): JobPosting =
  JobPosting(
    id = id,
    title = "Untitled",
    company = "Unknown",
    location = "",
    workMode = WorkMode.ONSITE,
    seniority = Seniority.MID,
    sourceUrl = URI.create("about:blank"),
  )
}
