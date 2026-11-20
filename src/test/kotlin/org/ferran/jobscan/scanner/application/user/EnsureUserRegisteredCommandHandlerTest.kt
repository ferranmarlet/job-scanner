package org.ferran.jobscan.scanner.application.user

import net.datafaker.Faker
import org.ferran.jobscan.scanner.domain.user.User
import org.ferran.jobscan.scanner.domain.user.UserAlreadyRegisteredError
import org.ferran.jobscan.scanner.domain.user.UserIdentityProvider
import org.ferran.jobscan.scanner.domain.user.UserRepository
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@Tag("unit")
class EnsureUserRegisteredCommandHandlerTest {

    private val userRepository = InMemoryUserRepository()
    private val identityProvider: UserIdentityProvider = mock()

    private val faker = Faker()

    private val sut = EnsureUserRegisteredCommandHandler(userRepository, identityProvider)

    @Test
    fun `creates the user with the email from the identity provider when no user has the external id`() {
        val command = anEnsureUserRegisteredCommand()
        val email = faker.internet().emailAddress()
        `when`(identityProvider.findEmail(command.accessToken)).thenReturn(email)

        sut.execute(command)

        val savedUser = assertNotNull(userRepository.findByExternalId(command.externalId))
        assertEquals(email, savedUser.email)
        assertFalse(savedUser.autoAnalysisEnabled)
    }

    @Test
    fun `leaves the existing user untouched when a user already has the external id`() {
        val command = anEnsureUserRegisteredCommand()
        val existingUser = User(
            externalId = command.externalId,
            email = faker.internet().emailAddress(),
            autoAnalysisEnabled = true,
        )
        userRepository.save(existingUser)
        `when`(identityProvider.findEmail(command.accessToken)).thenReturn(faker.internet().emailAddress())

        sut.execute(command)

        assertEquals(listOf(existingUser), userRepository.users)
        verifyNoInteractions(identityProvider)
    }

    @Test
    fun `succeeds when a concurrent request registers the same user first`() {
        val command = anEnsureUserRegisteredCommand()
        `when`(identityProvider.findEmail(command.accessToken)).thenReturn(faker.internet().emailAddress())
        userRepository.simulateConcurrentRegistrationOf(command.externalId)

        sut.execute(command)
    }

    private fun anEnsureUserRegisteredCommand() =
        EnsureUserRegisteredCommand(
            externalId = faker.internet().uuid(),
            accessToken = faker.internet().password(),
        )

    private class InMemoryUserRepository : UserRepository {
        val users = mutableListOf<User>()
        private val concurrentlyRegisteredExternalIds = mutableSetOf<String>()

        fun simulateConcurrentRegistrationOf(externalId: String) {
            concurrentlyRegisteredExternalIds += externalId
        }

        override fun save(user: User) {
            if (user.externalId in concurrentlyRegisteredExternalIds) throw UserAlreadyRegisteredError(user.externalId)
            users += user
        }

        override fun findByExternalId(externalId: String): User? =
            users.find { it.externalId == externalId }
    }
}
