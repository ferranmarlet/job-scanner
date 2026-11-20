package org.ferran.jobscan.scanner.application.user

import org.ferran.jobscan.scanner.domain.user.User
import org.ferran.jobscan.scanner.domain.user.UserAlreadyRegisteredError
import org.ferran.jobscan.scanner.domain.user.UserIdentityProvider
import org.ferran.jobscan.scanner.domain.user.UserRepository
import org.springframework.stereotype.Service

data class EnsureUserRegisteredCommand(
    val externalId: String,
    val accessToken: String,
)

@Service
class EnsureUserRegisteredCommandHandler(
    private val userRepository: UserRepository,
    private val identityProvider: UserIdentityProvider,
) {

    fun execute(command: EnsureUserRegisteredCommand) {
        if (userRepository.findByExternalId(command.externalId) != null) return

        val user = User(
            externalId = command.externalId,
            email = identityProvider.findEmail(command.accessToken),
            autoAnalysisEnabled = false,
        )

        saveUnlessRegisteredConcurrently(user)
    }

    private fun saveUnlessRegisteredConcurrently(user: User) {
        try {
            userRepository.save(user)
        } catch (_: UserAlreadyRegisteredError) {
        }
    }
}
