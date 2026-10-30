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
  fun `database rejects a second row with the same external source and external id`() {
    sut.save(posting())
    flushAndClear()

    entityManager.persist(JobPostingEntityMapper.toEntity(posting(url = "https://example.com/jobs/2")))

    assertFailsWith<ConstraintViolationException> { entityManager.flush() }
  }

  @Test
  fun `updates the stored posting when the external source and external id match`() {
    val stored = posting()
    sut.save(stored)
    flushAndClear()

    val imported = posting(url = "https://example.com/jobs/2").copy(
      title = "Mentat",
      skills = setOf("computation"),
      status = PostingStatus.OPEN,
      discoveredAt = Instant.parse("2026-11-01T10:00:00Z"),
    )
    sut.save(imported)
    flushAndClear()

    val expected = imported.copy(id = stored.id, status = stored.status, discoveredAt = stored.discoveredAt)
    assertEquals(expected, sut.findById(stored.id))
    assertNull(sut.findById(imported.id))
  }

  @Test
  fun `saving a posting under its own id persists its status change`() {
    val stored = posting().copy(status = PostingStatus.OPEN)
    sut.save(stored)
    flushAndClear()

    sut.save(stored.markApplied())
    flushAndClear()

    assertEquals(PostingStatus.APPLIED, sut.findById(stored.id)?.status)
  }

  @Test
  fun `stores postings without an external id as separate postings`() {
    val first = posting(externalId = null)
    val second = posting(url = "https://example.com/jobs/2", externalId = null)

    sut.save(first)
    sut.save(second)
    flushAndClear()

    assertEquals(first, sut.findById(first.id))
    assertEquals(second, sut.findById(second.id))
  }

  @Test
  fun `allows the same external id from a different external source`() {
    sut.save(posting())
    flushAndClear()

    sut.save(posting(url = "https://example.com/jobs/2", externalSourceId = "other"))

    entityManager.flush()
  }
}
