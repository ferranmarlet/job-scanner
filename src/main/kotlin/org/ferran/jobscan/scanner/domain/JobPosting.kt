package org.ferran.jobscan.scanner.domain

import java.net.URI
import java.time.Instant
import java.util.UUID

@JvmInline
value class JobPostingId(val value: UUID) {
  companion object {
    fun new() = JobPostingId(UUID.randomUUID())
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
  val title: String,
  val company: String,
  val location: String,
  val workMode: WorkMode,
  val seniority: Seniority,
  val salary: SalaryRange? = null,
  val skills: Set<String> = emptySet(),
  val sourceUrl: URI,
  val status: PostingStatus = PostingStatus.OPEN,
  val discoveredAt: Instant = Instant.now(),
) {
  init {
    require(title.isNotBlank()) { "Title must not be blank" }
    require(company.isNotBlank()) { "Company must not be blank" }
  }

  fun matchScore(wanted: Set<String>): Double =
  if (wanted.isEmpty()) 0.0
  else skills.count { it.lowercase() in wanted.map(String::lowercase) }.toDouble() / wanted.size

  fun markApplied(): JobPosting {
    check(status == PostingStatus.OPEN) { "Can only apply to an open posting, was $status" }
    return copy(status = PostingStatus.APPLIED)
  }
}
