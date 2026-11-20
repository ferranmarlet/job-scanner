package org.ferran.jobscan.scanner.infrastructure.persistence

import jakarta.persistence.EntityManager
import net.datafaker.Faker
import org.ferran.jobscan.PostgresTestConfiguration
import org.ferran.jobscan.scanner.domain.user.User
import org.ferran.jobscan.scanner.domain.user.UserAlreadyRegisteredError
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresTestConfiguration::class, JpaUserRepository::class)
class JpaUserRepositoryTest {
    @Autowired
    private lateinit var sut: JpaUserRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    val faker = Faker()

    private fun flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

    @Test
    fun `stores a user and finds it by external id`() {
        val user = User(
            externalId = faker.internet().uuid(),
            email = faker.internet().emailAddress(),
            autoAnalysisEnabled = true,
            createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS),
        )

        sut.save(user)
        flushAndClear()

        assertEquals(user, sut.findByExternalId(user.externalId))
    }

    @Test
    fun `returns null when no user has the external id`() {
        sut.save(
            User(
                externalId = faker.internet().uuid(),
                email = faker.internet().emailAddress(),
                autoAnalysisEnabled = false,
            )
        )
        flushAndClear()

        assertNull(sut.findByExternalId(faker.internet().uuid()))
    }

    @Test
    fun `throws UserAlreadyRegisteredError when another user already has the external id`() {
        val externalId = faker.internet().uuid()
        sut.save(User(externalId = externalId, email = faker.internet().emailAddress(), autoAnalysisEnabled = false))
        flushAndClear()

        assertFailsWith<UserAlreadyRegisteredError> {
            sut.save(User(externalId = externalId, email = faker.internet().emailAddress(), autoAnalysisEnabled = false))
        }
    }
}
