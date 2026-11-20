package org.ferran.jobscan.scanner.infrastructure.entrypoint

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.ferran.jobscan.scanner.application.user.EnsureUserRegisteredCommand
import org.ferran.jobscan.scanner.application.user.EnsureUserRegisteredCommandHandler
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.servlet.HandlerInterceptor

class UserRegistrationInterceptor(
    private val ensureUserRegistered: EnsureUserRegisteredCommandHandler,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val jwt = (SecurityContextHolder.getContext().authentication as JwtAuthenticationToken).token

        ensureUserRegistered.execute(EnsureUserRegisteredCommand(externalId = jwt.subject!!, accessToken = jwt.tokenValue))

        return true
    }
}
