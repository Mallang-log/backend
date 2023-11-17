package com.mallang.auth.presentation.support

import org.springframework.core.MethodParameter
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthArgumentResolver(
        private val authContext: AuthContext
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(Auth::class.java)
                && parameter.parameterType == Long::class.javaObjectType  // TODO: Change
    }

    override fun resolveArgument(
            parameter: MethodParameter,
            @Nullable mavContainer: ModelAndViewContainer?,
            webRequest: NativeWebRequest,
            @Nullable binderFactory: WebDataBinderFactory?
    ): Any {
        return authContext.getMemberId()
    }
}
