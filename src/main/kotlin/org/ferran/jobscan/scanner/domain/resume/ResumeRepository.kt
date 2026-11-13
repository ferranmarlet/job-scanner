package org.ferran.jobscan.scanner.domain.resume

import org.ferran.jobscan.scanner.domain.user.UserId

interface ResumeRepository {

    fun save(resume: Resume)

    fun findById(id: ResumeId): Resume?

    fun findByUserId(userId: UserId): Resume?
}
