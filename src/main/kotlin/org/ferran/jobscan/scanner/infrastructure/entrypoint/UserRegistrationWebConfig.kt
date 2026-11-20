package org.ferran.jobscan.scanner.infrastructure.entrypoint

import org.ferran.jobscan.scanner.application.user.EnsureUserRegisteredCommandHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class UserRegistrationWebConfig {

    @Bean
    fun userRegistrationWebMvcConfigurer(ensureUserRegistered: EnsureUserRegisteredCommandHandler): WebMvcConfigurer =
        object : WebMvcConfigurer {
            override fun addInterceptors(registry: InterceptorRegistry) {
                registry.addInterceptor(UserRegistrationInterceptor(ensureUserRegistered))
            }
        }
}
