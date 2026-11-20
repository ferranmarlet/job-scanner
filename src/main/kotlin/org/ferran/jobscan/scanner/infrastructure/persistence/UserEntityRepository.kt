package org.ferran.jobscan.scanner.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserEntityRepository : JpaRepository<UserEntity, UUID> {

    fun findByExternalId(externalId: String): UserEntity?
}
