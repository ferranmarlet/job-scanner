package org.ferran.jobscan.scanner.domain

import net.datafaker.Faker
import java.net.URI

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
            )
    }
}
