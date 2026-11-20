package org.ferran.jobscan.scanner.domain.user

interface UserIdentityProvider {

    fun findEmail(accessToken: String): String
}
