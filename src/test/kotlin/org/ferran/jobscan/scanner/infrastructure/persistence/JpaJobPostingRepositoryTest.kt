package org.ferran.jobscan.scanner.infrastructure.persistence

import jakarta.persistence.EntityManager
import org.ferran.jobscan.PostgresTestConfiguration
import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.JobPostingId
import org.ferran.jobscan.scanner.domain.PostingStatus
import org.ferran.jobscan.scanner.domain.SalaryRange
import org.ferran.jobscan.scanner.domain.Seniority
import org.ferran.jobscan.scanner.domain.WorkMode
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import java.net.URI
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresTestConfiguration::class, JpaJobPostingRepository::class)
class JpaJobPostingRepositoryTest {

  @Autowired
  private lateinit var sut: JpaJobPostingRepository

  @Autowired
  private lateinit var entityManager: EntityManager

  private fun posting(
    url: String = "https://example.com/jobs/1",
    externalId: String? = "job-1",
    externalSourceId: String = "findwork",
  ) = JobPosting(
    externalId = externalId,
    externalSourceId = externalSourceId,
    title = "Guild Navigator",
    company = "Spacing Guild",
    location = "Arrakis",
    workMode = WorkMode.HYBRID,
    seniority = Seniority.SENIOR,
    salary = SalaryRange(min = 60_000, max = 80_000, currency = "EUR"),
    skills = setOf("prescience", "future sight"),
    sourceUrl = URI.create(url),
    status = PostingStatus.APPLIED,
    discoveredAt = Instant.parse("2026-10-30T09:15:00Z"),
  )

  private fun flushAndClear() {
    entityManager.flush()
    entityManager.clear()
  }

  @Test
  fun `stores and loads every field`() {
    val posting = posting()

    sut.save(posting)
    flushAndClear()

    assertEquals(posting, sut.findById(posting.id))
  }

  @Test
  fun `loads a posting without salary or skills`() {
    val posting = posting().copy(salary = null, skills = emptySet(), company = null, workMode = null)

    sut.save(posting)
    flushAndClear()

    assertEquals(posting, sut.findById(posting.id))
  }

  @Test
  fun `returns null when the posting does not exist`() {
    assertNull(sut.findById(JobPostingId.new()))
  }

  @Test
  fun `finds out whether a source url exists`() {
    val posting = posting()

    sut.save(posting)
    flushAndClear()

    assertTrue(sut.existsBySourceUrl(posting.sourceUrl))
    assertFalse(sut.existsBySourceUrl(URI.create("https://example.com/jobs/other")))
  }

  @Test
  fun `rejects a second posting with the same source url`() {
    sut.save(posting())
    flushAndClear()

    sut.save(posting(externalId = "job-2"))

    assertFailsWith<ConstraintViolationException> { entityManager.flush() }
  }

  @Test
  fun `rejects a second posting with the same external source and external id`() {
    sut.save(posting())
    flushAndClear()

    sut.save(posting(url = "https://example.com/jobs/2"))

    assertFailsWith<ConstraintViolationException> { entityManager.flush() }
  }

  @Test
  fun `allows the same external id from a different external source`() {
    sut.save(posting())
    flushAndClear()

    sut.save(posting(url = "https://example.com/jobs/2", externalSourceId = "other"))

    entityManager.flush()
  }
}
