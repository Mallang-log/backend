package com.mallang.auth.presentation.support

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class ExtractAuthenticationInterceptor(
        private val authContext: AuthContext
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.getSession(false)
                ?.getAttribute(MEMBER_ID)
                ?.let { memberId -> authContext.setMemberId(memberId as Long) }
        return true
    }
}
