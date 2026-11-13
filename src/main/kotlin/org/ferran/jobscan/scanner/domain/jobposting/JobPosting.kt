package org.ferran.jobscan.scanner.domain.jobposting

import java.net.URI
import java.time.Instant
import java.util.UUID

@JvmInline
value class JobPostingId(val value: UUID) {
    companion object {
        fun new() =
            JobPostingId(UUID.randomUUID())
    }
}

enum class Seniority { JUNIOR, MID, SENIOR, LEAD }

enum class WorkMode { ONSITE, HYBRID, REMOTE }

enum class PostingStatus { OPEN, APPLIED, REJECTED, CLOSED }

data class SalaryRange(val min: Int, val max: Int, val currency: String) {
    init {
        require(min in 0..max) { "Salary min must be between 0 and max" }
        require(currency.length == 3) { "Currency must be an ISO 4217 code" }
    }
}

data class JobPosting(
    val id: JobPostingId = JobPostingId.new(),
    val externalId: String?,
    val externalSourceId: String,
    val title: String,
    val company: String?,
    val location: String?,
    val workMode: WorkMode?,
    val seniority: Seniority?,
    val salary: SalaryRange? = null,
    val skills: Set<String> = emptySet(),
    val sourceUrl: URI,
    val status: PostingStatus = PostingStatus.OPEN,
    val discoveredAt: Instant = Instant.now(),
) {
    init {
        require(title.isNotBlank()) { "Title must not be blank" }
    }

    fun matchScore(wanted: Set<String>): Double =
        if (wanted.isEmpty()) 0.0
        else skills.count { it.lowercase() in wanted.map(String::lowercase) }.toDouble() / wanted.size

    fun markApplied(): JobPosting =
        changeStatus(PostingStatus.APPLIED)

    fun changeStatus(newStatus: PostingStatus): JobPosting {
        if (!canTransitionTo(newStatus)) throw InvalidStatusTransitionError(status, newStatus)
        return copy(status = newStatus)
    }

    private fun canTransitionTo(target: PostingStatus): Boolean =
        when (status) {
            PostingStatus.OPEN -> target in setOf(PostingStatus.APPLIED, PostingStatus.CLOSED)
            PostingStatus.APPLIED -> target in setOf(PostingStatus.REJECTED, PostingStatus.CLOSED)
            PostingStatus.REJECTED, PostingStatus.CLOSED -> false
        }
}
