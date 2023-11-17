package com.mallang.auth.domain.oauth

import com.mallang.auth.domain.OauthServerType
import com.mallang.auth.exception.UnsupportedOauthTypeException
import org.springframework.stereotype.Component
import java.util.function.Function
import java.util.stream.Collectors

@Component
class AuthCodeRequestUrlProviderComposite(providers: Set<AuthCodeRequestUrlProvider>) {

    private val mapping: Map<OauthServerType, AuthCodeRequestUrlProvider> = providers.stream()
            .collect(Collectors.toMap({ it.supportServer() }, Function.identity()))

    fun provide(oauthServerType: OauthServerType): String {
        return getProvider(oauthServerType).provide()
    }

    private fun getProvider(oauthServerType: OauthServerType): AuthCodeRequestUrlProvider {
        return mapping[oauthServerType] ?: throw UnsupportedOauthTypeException()
    }
}
