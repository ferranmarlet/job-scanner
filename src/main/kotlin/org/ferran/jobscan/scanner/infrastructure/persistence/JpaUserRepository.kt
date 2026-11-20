package org.ferran.jobscan.scanner.infrastructure.persistence

import org.ferran.jobscan.scanner.domain.user.User
import org.ferran.jobscan.scanner.domain.user.UserAlreadyRegisteredError
import org.ferran.jobscan.scanner.domain.user.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class JpaUserRepository(
    private val jpaRepository: UserEntityRepository,
) : UserRepository {

    override fun save(user: User) {
        try {
            jpaRepository.saveAndFlush(UserEntityMapper.toEntity(user))
        } catch (_: DataIntegrityViolationException) {
            throw UserAlreadyRegisteredError(user.externalId)
        }
    }

    @Transactional(readOnly = true)
    override fun findByExternalId(externalId: String): User? =
        jpaRepository.findByExternalId(externalId)?.let(UserEntityMapper::toDomain)
}
