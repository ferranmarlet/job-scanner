package org.ferran.jobscan.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimValidator
import org.springframework.security.web.SecurityFilterChain

@ConfigurationProperties("jobscan.security")
data class SecurityProperties(
    // Cognito app client the tokens must be issued to
    val cognitoClientId: String,
)

/**
 * Every endpoint requires a Cognito access token sent as a Bearer token.
 * Importing job postings also requires membership of the `admin` group.
 *
 * The issuer, signature and expiry are checked by the decoder that Spring Boot builds from
 * `spring.security.oauth2.resourceserver.jwt.issuer-uri`; the validator beans below are added to it.
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize(HttpMethod.POST, "/import-job-postings", hasRole(ADMIN_ROLE))
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer { jwt { } }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            // No cookies or sessions: the token travels in the Authorization header, so CSRF doesn't apply
            csrf { disable() }
        }
        return http.build()
    }

    // Cognito access tokens have no `aud` claim; the app client is in `client_id` instead
    @Bean
    fun clientIdValidator(properties: SecurityProperties): OAuth2TokenValidator<Jwt> =
        JwtClaimValidator<String>("client_id") { it == properties.cognitoClientId }

    // Rejects Cognito ID tokens, which are signed by the same pool but are not meant for APIs
    @Bean
    fun accessTokenValidator(): OAuth2TokenValidator<Jwt> =
        JwtClaimValidator<String>("token_use") { it == "access" }

    companion object {
        const val ADMIN_ROLE = "admin"
    }
}
