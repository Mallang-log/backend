package com.mallang.auth.config

import com.mallang.auth.presentation.support.AuthArgumentResolver
import com.mallang.auth.presentation.support.AuthInterceptor
import com.mallang.auth.presentation.support.ExtractAuthenticationInterceptor
import com.mallang.auth.presentation.support.OptionalAuthArgumentResolver
import com.mallang.common.presentation.UriAndMethodAndParamCondition
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class AuthConfig(
        private val authInterceptor: AuthInterceptor,
        private val extractAuthenticationInterceptor: ExtractAuthenticationInterceptor,
        private val authArgumentResolver: AuthArgumentResolver,
        private val optionalAuthArgumentResolver: OptionalAuthArgumentResolver
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(extractAuthenticationInterceptor)
                .addPathPatterns("/**")
                .order(0)
        registry.addInterceptor(setUpAuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/oauth/**")
                .order(1)
    }

    private fun setUpAuthInterceptor(): AuthInterceptor {
        this.authInterceptor.setNoAuthRequiredConditions(
                UriAndMethodAndParamCondition(
                        uriPatterns = setOf("/members/**"),
                        httpMethods = mutableSetOf(GET)
                ),
                UriAndMethodAndParamCondition(
                        uriPatterns = setOf("/posts/**"),
                        httpMethods = mutableSetOf(GET)
                ),
                UriAndMethodAndParamCondition(
                        uriPatterns = setOf("/categories"),
                        httpMethods = mutableSetOf(GET)
                ),
                UriAndMethodAndParamCondition(
                        uriPatterns = setOf("/categories"),
                        httpMethods = mutableSetOf(GET)
                ),
                UriAndMethodAndParamCondition(
                        uriPatterns = setOf("/comments/**"),
                        httpMethods = mutableSetOf(POST, PUT, DELETE),
                        params = mapOf("unauthenticated" to "true")
                ),
                UriAndMethodAndParamCondition(
                        uriPatterns = setOf("/comments"),
                        httpMethods = mutableSetOf(GET)
                ),
                UriAndMethodAndParamCondition(
                        uriPatterns = setOf("/blog-subscribes/*"),
                        httpMethods = mutableSetOf(GET)
                )
        )
        return authInterceptor
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authArgumentResolver)
        resolvers.add(optionalAuthArgumentResolver)
    }
}
