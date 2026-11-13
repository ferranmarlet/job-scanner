package org.ferran.jobscan.scanner.domain.resume

import org.ferran.jobscan.scanner.domain.user.UserId
import java.time.Instant
import java.util.UUID

@JvmInline
value class ResumeId(val value: UUID) {
    companion object {
        fun new() =
            ResumeId(UUID.randomUUID())
    }
}

data class Resume(
    val id: ResumeId = ResumeId.new(),
    val userId: UserId,
    val fileLink: String,
    val createdAt: Instant = Instant.now(),
)
