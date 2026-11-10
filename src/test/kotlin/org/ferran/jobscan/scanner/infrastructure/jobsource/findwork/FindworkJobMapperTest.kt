package org.ferran.jobscan.scanner.infrastructure.jobsource.findwork

import net.datafaker.Faker
import org.assertj.core.api.Assertions.assertThat
import org.ferran.jobscan.scanner.domain.JobPosting
import org.ferran.jobscan.scanner.domain.WorkMode
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.net.URI
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Tag("unit")
class FindworkJobMapperTest {

    @Test
    fun  `should map response to JobPosting domain entity`() {
        val faker = Faker()

        val findworkJobDto = FindworkJobDto(
            Random.nextLong(1, 1000).toString(),
            faker.lorem().word(),
            faker.company().name(),
            faker.address().city(),
            faker.bool().bool(),
            faker.internet().url(),
            List(2, { faker.lorem().word() }),
            faker.timeAndDate().past(30, TimeUnit.DAYS),
        )
        val jobPosting = findworkJobDto.let(FindworkJobMapper::toDomain)

        val expected = JobPosting(
            externalId = findworkJobDto.id,
            externalSourceId = FindworkJobSourceRepository.EXTERNAL_JOB_SOURCE_ID,
            title = findworkJobDto.role!!,
            company = findworkJobDto.companyName,
            location = findworkJobDto.location,
            workMode = if (findworkJobDto.remote) WorkMode.REMOTE else WorkMode.ONSITE,
            seniority = null,
            skills = findworkJobDto.keywords.toSet(),
            sourceUrl = URI.create(findworkJobDto.url!!),
        )
        assertThat(jobPosting)
            .usingRecursiveComparison()
            .ignoringFields("id", "discoveredAt")
            .isEqualTo(expected)
    }
}
