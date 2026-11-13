package org.ferran.jobscan.scanner.domain.user

interface UserRepository {

    fun save(user: User)

    fun findByExternalId(externalId: String): User?
}
