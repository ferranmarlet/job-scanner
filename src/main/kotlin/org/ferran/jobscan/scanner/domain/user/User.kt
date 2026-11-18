package org.ferran.jobscan.scanner.domain.user

import org.ferran.jobscan.scanner.domain.resume.Resume
import java.time.Instant
import java.util.UUID

@JvmInline
value class UserId(val value: UUID) {
    companion object {
        fun new() =
            UserId(UUID.randomUUID())
    }
}

data class User(
    val id: UserId = UserId.new(),
    val externalId: String,
    val autoAnalysisEnabled: Boolean,
    val resume: Resume? = null,
    val createdAt: Instant = Instant.now(),
)
