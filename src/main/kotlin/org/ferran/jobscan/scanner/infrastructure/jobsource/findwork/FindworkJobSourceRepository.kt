package org.ferran.jobscan.scanner.infrastructure.jobsource.findwork

import org.ferran.jobscan.scanner.domain.ExternalJobSourceRepository
import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobSearchCriteria
import org.ferran.jobscan.scanner.domain.WorkMode
import org.ferran.jobscan.scanner.infrastructure.ScannerProperties
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class FindworkJobSourceRepository(properties: ScannerProperties) : ExternalJobSourceRepository {
    companion object {
        const val EXTERNAL_JOB_SOURCE_ID = "findwork"
    }

    private val client: RestClient = RestClient.builder()
        .baseUrl(properties.findworkBaseUrl)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Token ${properties.findworkApiKey}")
        .build()

    override fun findJobPostings(criteria: JobSearchCriteria): List<JobPosting> {
        val response = client.get()
            .uri { builder ->
                builder.path("/jobs/")
                if (criteria.keywords.isNotEmpty()) {
                    builder.queryParam("search", criteria.keywords.joinToString(" "))
                }
                criteria.location?.let { builder.queryParam("location", it) }
                remoteFilter(criteria)?.let { builder.queryParam("remote", it) }
                builder.queryParam("sort_by", "date")
                builder.build()
            }
            .retrieve()
            .body<FindworkJobsResponse>()
            ?: return emptyList()

        // Findwork has no date filter!
        return response.results
            .filter { dto -> criteria.postedAfter == null || dto.datePosted?.let { it > criteria.postedAfter } == true }
            .mapNotNull(FindworkJobMapper::toDomain)
    }

    private fun remoteFilter(criteria: JobSearchCriteria): Boolean? =
        when {
            criteria.workMode == WorkMode.REMOTE -> true
            else -> false
        }
}
