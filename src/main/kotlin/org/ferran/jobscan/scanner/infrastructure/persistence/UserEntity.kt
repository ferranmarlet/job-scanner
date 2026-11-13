package org.ferran.jobscan.scanner.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "users",
)
class UserEntity(
    @Id
    var id: UUID,

    @Column(nullable = false, unique = true)
    var externalId: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,
)
