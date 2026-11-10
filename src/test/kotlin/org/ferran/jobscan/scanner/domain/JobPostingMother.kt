package org.ferran.jobscan.scanner.domain

import net.datafaker.Faker
import java.net.URI
import java.time.temporal.ChronoUnit

val faker = Faker()

class JobPostingMother {
    companion object {
        fun create(
            id: JobPostingId = JobPostingId.new(),
            externalId: String? = faker.lorem().characters(),
            externalSourceId: String = faker.company().name(),
            title: String = faker.finalFantasyXIV().job(),
            company: String? = faker.company().name(),
            location: String? = faker.address().cityName(),
            workMode: WorkMode? = WorkMode.entries.random(),
            seniority: Seniority? = Seniority.entries.random(),
            sourceUrl: String = faker.internet().url(),
            status: PostingStatus = PostingStatus.entries.random(),
        ) = 
            JobPosting(
                id = id,
                externalId = externalId,
                externalSourceId = externalSourceId,
                title = title,
                company = company,
                location = location,
                workMode = workMode,
                seniority = seniority,
                sourceUrl = URI.create(sourceUrl),
                discoveredAt = faker.timeAndDate().past().truncatedTo(ChronoUnit.SECONDS),
                status = status,
            )
    }
}
