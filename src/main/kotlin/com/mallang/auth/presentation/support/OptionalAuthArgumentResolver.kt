package com.mallang.auth.presentation.support

import jakarta.annotation.Nullable
import lombok.RequiredArgsConstructor
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@RequiredArgsConstructor
@Component
class OptionalAuthArgumentResolver(
        private val authContext: AuthContext
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(OptionalAuth::class.java)
                && parameter.getParameterType() == Long::class.javaObjectType
    }

    @Nullable
    override fun resolveArgument(
            parameter: MethodParameter,
            mavContainer: ModelAndViewContainer?,
            webRequest: NativeWebRequest,
            binderFactory: WebDataBinderFactory?
    ): Any? {
        return if (authContext.unAuthenticated()) null
        else authContext.getMemberId()
    }
}
