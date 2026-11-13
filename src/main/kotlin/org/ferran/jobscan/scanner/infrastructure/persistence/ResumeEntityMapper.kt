package org.ferran.jobscan.scanner.infrastructure.persistence

import org.ferran.jobscan.scanner.domain.resume.Resume
import org.ferran.jobscan.scanner.domain.resume.ResumeId
import org.ferran.jobscan.scanner.domain.user.UserId

object ResumeEntityMapper {

    fun toEntity(resume: Resume): ResumeEntity =
        ResumeEntity(
            resume.id.value,
            resume.userId.value,
            resume.fileLink,
            resume.createdAt,
        )

    fun toDomain(entity: ResumeEntity): Resume =
        Resume(
            id = ResumeId(entity.id),
            userId = UserId(entity.userId),
            fileLink = entity.fileLink,
            createdAt = entity.createdAt,
        )
}
