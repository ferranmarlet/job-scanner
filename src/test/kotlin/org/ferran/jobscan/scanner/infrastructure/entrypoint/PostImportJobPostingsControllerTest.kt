package org.ferran.jobscan.scanner.infrastructure.entrypoint

import net.datafaker.Faker
import org.ferran.jobscan.scanner.application.ImportJobPostingsCommand
import org.ferran.jobscan.scanner.application.ImportJobPostingsCommandHandler
import org.ferran.jobscan.scanner.domain.jobposting.JobSearchCriteria
import org.ferran.jobscan.scanner.domain.jobposting.WorkMode
import org.ferran.jobscan.security.SecurityConfig
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import tools.jackson.databind.json.JsonMapper

@WebMvcTest(PostImportJobPostingsController::class)
@Import(SecurityConfig::class)
@WithMockUser(roles = [SecurityConfig.ADMIN_ROLE])
class PostImportJobPostingsControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @MockitoBean
    private lateinit var importJobPostingsCommandHandler: ImportJobPostingsCommandHandler

    val faker = Faker()

    @Test
    fun `constructs command and calls handler`() {
        val request = ImportJobPostingsRequest(
            listOf(
                faker.starWars().droids(),
                faker.finalFantasyXIV().job()
            ),
            faker.address().city(),
            faker.timeAndDate().past(),
            WorkMode.entries.random(),
        )

        val expectedCommand = ImportJobPostingsCommand(
            JobSearchCriteria(
                request.keywords,
                request.location,
                request.postedAfter,
                request.workMode,
            )
        )

        mockMvc.post("/import-job-postings") {
            contentType = MediaType.APPLICATION_JSON
            content = jsonMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }

        verify(importJobPostingsCommandHandler).execute(
            expectedCommand
        )
    }

    @Test
    fun `returns 400 when the request body is missing`() {
        mockMvc.post("/import-job-postings").andExpect {
            status { isBadRequest() }
        }

        verifyNoInteractions(importJobPostingsCommandHandler)
    }
}
