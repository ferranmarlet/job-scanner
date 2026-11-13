package org.ferran.jobscan.scanner.infrastructure.persistence

import org.ferran.jobscan.scanner.domain.jobposting.PostingStatus
import org.ferran.jobscan.scanner.domain.jobposting.WorkMode
import org.springframework.data.jpa.domain.Specification

object JobPostingSpecs {
    fun hasExternalSourceId(externalSourceId: String?) =
        Specification<JobPostingEntity> { root,
                                          _, cb ->
            externalSourceId?.let { cb.equal(root.get<String>("externalSourceId"), it) }
        }

    fun hasWorkMode(workMode: WorkMode?) =
        Specification<JobPostingEntity> {
                root, _, cb ->
            workMode?.let { cb.equal(root.get<WorkMode>("workMode"), it) }
        }

    fun hasStatus(status: PostingStatus?) =
        Specification<JobPostingEntity> {
                root, _, cb ->
            status?.let { cb.equal(root.get<PostingStatus>("status"), it) }
        }
}
