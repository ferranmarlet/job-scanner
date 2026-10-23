package org.ferran.jobscan.scanner.infrastructure.persistence

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import org.ferran.jobscan.scanner.domain.PostingStatus
import org.ferran.jobscan.scanner.domain.Seniority
import org.ferran.jobscan.scanner.domain.WorkMode
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "job_posting")
class JobPostingEntity(
  @Id
  var id: UUID,

  @Column(nullable = false)
  var title: String,

  var company: String?,

  var location: String?,

  @Enumerated(EnumType.STRING)
  @Column(name = "work_mode")
  var workMode: WorkMode?,

  @Enumerated(EnumType.STRING)
  var seniority: Seniority?,

  @Embedded
  var salary: SalaryEmbeddable?,

  @ElementCollection
  @CollectionTable(name = "job_posting_skill", joinColumns = [JoinColumn(name = "job_posting_id")])
  @Column(name = "skill", nullable = false)
  var skills: MutableSet<String>,

  @Column(name = "source_url", nullable = false, unique = true)
  var sourceUrl: String,

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  var status: PostingStatus,

  @Column(name = "discovered_at", nullable = false)
  var discoveredAt: Instant,
)

@Embeddable
class SalaryEmbeddable(
  @Column(name = "salary_min")
  var min: Int,

  @Column(name = "salary_max")
  var max: Int,

  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "salary_currency", length = 3)
  var currency: String,
)
