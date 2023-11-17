package com.mallang.auth.presentation.support

import com.mallang.auth.exception.NoAuthenticationSessionException
import com.mallang.common.presentation.UriAndMethodAndParamCondition
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component
import org.springframework.util.PathMatcher
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthInterceptor(
        private val pathMatcher: ObjectProvider<PathMatcher>,
        private val authContext: AuthContext,
        private val noAuthRequiredConditions: MutableSet<UriAndMethodAndParamCondition> = HashSet()
) : HandlerInterceptor {

    fun setNoAuthRequiredConditions(vararg noAuthRequiredConditions: UriAndMethodAndParamCondition) {
        this.noAuthRequiredConditions.clear()
        this.noAuthRequiredConditions.addAll(noAuthRequiredConditions.asList())
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (authenticationRequired(request) && authContext.unAuthenticated()) {
            throw NoAuthenticationSessionException()
        }
        return true
    }

    private fun authenticationRequired(request: HttpServletRequest): Boolean {
        return noAuthRequiredConditions.none { it.match(pathMatcher.getIfAvailable(), request) }
    }
}
