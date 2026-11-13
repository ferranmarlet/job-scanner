package org.ferran.jobscan.scanner.infrastructure.persistence

import org.ferran.jobscan.scanner.domain.user.User
import org.ferran.jobscan.scanner.domain.user.UserId

object UserEntityMapper {

    fun toEntity(user: User): UserEntity =
        UserEntity(
            user.id.value,
            user.externalId,
            user.createdAt,
        )

    fun toDomain(entity: UserEntity): User =
        User(
            id = UserId(entity.id),
            externalId = entity.externalId,
            createdAt = entity.createdAt,
        )
}
