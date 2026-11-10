package org.ferran.jobscan

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(PostgresTestConfiguration::class)
class JobscanApplicationTests {

    @Test
    fun contextLoads() {
    }

}
