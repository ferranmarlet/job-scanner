package org.ferran.jobscan.scanner.infrastructure.persistence

import jakarta.persistence.EntityManager
import net.datafaker.Faker
import org.ferran.jobscan.PostgresTestConfiguration
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingFilter
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingId
import org.ferran.jobscan.scanner.domain.jobposting.JobPostingMother
import org.ferran.jobscan.scanner.domain.jobposting.PostingStatus
import org.ferran.jobscan.scanner.domain.jobposting.WorkMode
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import java.net.URI
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

    val faker = Faker()

    private fun flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

    @Test
    fun `stores and loads every field`() {
        val posting = JobPostingMother.create()

        sut.save(posting)
        flushAndClear()

        assertEquals(posting, sut.findById(posting.id))
    }

    @Test
    fun `loads a posting without salary or skills`() {
        val posting = JobPostingMother.create().copy(salary = null, skills = emptySet(), company = null, workMode = null)

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
        val posting = JobPostingMother.create()

        sut.save(posting)
        flushAndClear()

        assertTrue(sut.existsBySourceUrl(posting.sourceUrl))
        assertFalse(sut.existsBySourceUrl(URI.create("https://example.com/jobs/other")))
    }

    @Test
    fun `rejects a second posting with the same source url`() {
        val url = faker.internet().url()
        sut.save(JobPostingMother.create(sourceUrl = url))
        flushAndClear()

        sut.save(JobPostingMother.create(sourceUrl = url, externalId = "job-2"))

        assertFailsWith<ConstraintViolationException> { entityManager.flush() }
    }

    @Test
    fun `database rejects a second row with the same external source and external id`() {
        val externalId = faker.lorem().characters()
        val externalSourceId = faker.dragonBall().character()
        sut.save(JobPostingMother.create(externalId = externalId, externalSourceId = externalSourceId))
        flushAndClear()

        entityManager.persist(JobPostingEntityMapper.toEntity(JobPostingMother.create(sourceUrl = "https://example.com/jobs/2", externalId = externalId, externalSourceId = externalSourceId)))

        assertFailsWith<ConstraintViolationException> { entityManager.flush() }
    }

    @Test
    fun `updates the stored posting when the external source and external id match`() {
        val externalId = faker.lorem().characters()
        val externalSourceId = faker.tron().character()
        val stored = JobPostingMother.create(externalId = externalId, externalSourceId = externalSourceId)
        sut.save(stored)
        flushAndClear()

        val imported =
            JobPostingMother.create(externalId = externalId, externalSourceId = externalSourceId).copy(
                title = faker.finalFantasyXIV().job(),
                skills = setOf(faker.tron().game()),
                status = PostingStatus.entries.random(),
                discoveredAt = faker.timeAndDate().past(),
            )
        sut.save(imported)
        flushAndClear()

        val expected = imported.copy(id = stored.id, status = stored.status, discoveredAt = stored.discoveredAt)

        val storedJobPosting = sut.findById(stored.id)
        assertEquals(expected.id, stored.id)
        assertEquals(expected.status, stored.status)
        assertEquals(expected.title, imported.title)
        assertEquals(expected.discoveredAt, stored.discoveredAt)
        assertNull(sut.findById(imported.id))
    }

    @Test
    fun `saving a posting under its own id persists its status change`() {
        val stored = JobPostingMother.create().copy(status = PostingStatus.OPEN)
        sut.save(stored)
        flushAndClear()

        sut.save(stored.markApplied())
        flushAndClear()

        assertEquals(PostingStatus.APPLIED, sut.findById(stored.id)?.status)
    }

    @Test
    fun `stores postings without an external id as separate postings`() {
        val first = JobPostingMother.create(externalId = null)
        val second = JobPostingMother.create(sourceUrl = "https://example.com/jobs/2", externalId = null)

        sut.save(first)
        sut.save(second)
        flushAndClear()

        assertEquals(first, sut.findById(first.id))
        assertEquals(second, sut.findById(second.id))
    }

    @Test
    fun `allows the same external id from a different external source`() {
        sut.save(JobPostingMother.create())
        flushAndClear()

        sut.save(JobPostingMother.create(sourceUrl = "https://example.com/jobs/2", externalSourceId = "other"))

        entityManager.flush()
    }

    @Test
    fun `returns paged response when getting multiple JobPostings`() {
        sut.save(JobPostingMother.create())
        sut.save(JobPostingMother.create())
        sut.save(JobPostingMother.create())
        flushAndClear()

        assertEquals(3, sut.findAll(JobPostingFilter(), null).totalElements)
        assertEquals(3, sut.findAll(JobPostingFilter(), null).elements.count())
    }
    
    @Test
    fun `returns JobPostings filtered by workMode`() {
        sut.save(JobPostingMother.create(workMode = WorkMode.REMOTE))
        sut.save(JobPostingMother.create(workMode = WorkMode.REMOTE))
        sut.save(JobPostingMother.create(workMode = WorkMode.HYBRID))
        flushAndClear()

        val workModeFilteredResult = sut.findAll(JobPostingFilter(workMode = WorkMode.REMOTE))
        assertEquals(2, workModeFilteredResult.totalElements)
        assertEquals(2, workModeFilteredResult.elements.count())
    }
    
    @Test
    fun `returns JobPostings filtered by status`() {
        sut.save(JobPostingMother.create(status = PostingStatus.OPEN))
        sut.save(JobPostingMother.create(status = PostingStatus.OPEN))
        sut.save(JobPostingMother.create(status = PostingStatus.APPLIED))
        flushAndClear()

        val statusFilteredResult = sut.findAll(JobPostingFilter(status = PostingStatus.APPLIED))
        assertEquals(1, statusFilteredResult.totalElements)
        assertEquals(1, statusFilteredResult.elements.count())
    }
    
    @Test
    fun `returns JobPostings filtered by source`() {
        sut.save(JobPostingMother.create(externalSourceId = faker.company().name()))
        flushAndClear()

        val sourceFilteredResult = sut.findAll(JobPostingFilter(externalSourceId = faker.dune().planet()))
        assertEquals(0, sourceFilteredResult.totalElements)
        assertEquals(0, sourceFilteredResult.elements.count())
    }
}
