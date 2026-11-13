package org.ferran.jobscan.scanner.infrastructure.persistence

import org.ferran.jobscan.scanner.domain.jobposting.JobPosting
import org.ferran.jobscan.scanner.domain.jobposting.PostingStatus
import org.ferran.jobscan.scanner.domain.jobposting.SalaryRange
import org.ferran.jobscan.scanner.domain.jobposting.Seniority
import org.ferran.jobscan.scanner.domain.jobposting.WorkMode
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Tag("unit")
class JobPostingEntityMapperTest {

    private val posting = JobPosting(
        externalId = "job-1",
        externalSourceId = "findwork",
        title = "Guild Navigator",
        company = "Spacing Guild",
        location = "Arrakis",
        workMode = WorkMode.HYBRID,
        seniority = Seniority.SENIOR,
        salary = SalaryRange(min = 60_000, max = 80_000, currency = "EUR"),
        skills = setOf("prescience", "future sight"),
        sourceUrl = URI.create("https://example.com/jobs/1"),
        status = PostingStatus.APPLIED,
        discoveredAt = Instant.parse("2026-10-30T09:15:00Z"),
    )

    @Test
    fun `maps every field to the entity and back`() {
        val result = JobPostingEntityMapper.toDomain(JobPostingEntityMapper.toEntity(posting))

        assertEquals(posting, result)
    }

    @Test
    fun `keeps optional fields null`() {
        val minimal = JobPosting(
            externalId = null,
            externalSourceId = "findwork",
            title = "Guild Navigator",
            company = null,
            location = null,
            workMode = null,
            seniority = null,
            sourceUrl = URI.create("https://example.com/jobs/2"),
        )

        val entity = JobPostingEntityMapper.toEntity(minimal)

        assertNull(entity.salary)
        assertEquals(minimal, JobPostingEntityMapper.toDomain(entity))
    }
}
