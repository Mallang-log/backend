package com.mallang.common.presentation

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpMethod
import org.springframework.util.PathMatcher

data class UriAndMethodAndParamCondition(
        val uriPatterns: Set<String>,
        val httpMethods: Set<HttpMethod>,
        val params: Map<String, String> = mutableMapOf()
) {

    fun match(pathMatcher: PathMatcher, request: HttpServletRequest): Boolean {
        return (matchURI(pathMatcher, request)
                && matchMethod(request)
                && matchParamIfRequired(request))
    }

    private fun matchURI(pathMatcher: PathMatcher, request: HttpServletRequest): Boolean {
        return uriPatterns.any { pattern: String -> pathMatcher.match(pattern, request.requestURI) }
    }

    private fun matchMethod(request: HttpServletRequest): Boolean {
        return httpMethods.contains(HttpMethod.valueOf(request.method))
    }

    private fun matchParamIfRequired(request: HttpServletRequest): Boolean {
        if (matchParamIsNotRequired()) {
            return true
        }
        return params.all { (key, value) -> value == request.getParameter(key) }
    }

    private fun matchParamIsNotRequired(): Boolean = params.isEmpty()
}
