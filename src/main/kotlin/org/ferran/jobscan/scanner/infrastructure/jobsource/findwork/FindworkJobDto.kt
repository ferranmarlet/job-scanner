package org.ferran.jobscan.scanner.infrastructure.jobsource.findwork

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class FindworkJobsResponse(
  val count: Int = 0,
  val next: String? = null,
  val results: List<FindworkJobDto> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FindworkJobDto(
  val id: String,
  val role: String?,
  @JsonProperty("company_name") val companyName: String?,
  val location: String?,
  val remote: Boolean = false,
  val url: String?,
  val keywords: List<String> = emptyList(),
  @JsonProperty("date_posted") val datePosted: Instant?,
)
