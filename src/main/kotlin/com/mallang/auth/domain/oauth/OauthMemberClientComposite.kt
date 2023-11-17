package com.mallang.auth.domain.oauth

import com.mallang.auth.domain.Member
import com.mallang.auth.domain.OauthServerType
import com.mallang.auth.exception.UnsupportedOauthTypeException
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.function.Function
import java.util.stream.Collectors

@Profile("!test")
@Component
class OauthMemberClientComposite(clients: Set<OauthMemberClient>) {

    private val mapping: Map<OauthServerType, OauthMemberClient> = clients.stream()
            .collect(Collectors.toMap({ it.supportServer() }, Function.identity()))

    fun fetch(oauthServerType: OauthServerType, authCode: String): Member {
        return getClient(oauthServerType).fetch(authCode)
    }

    private fun getClient(oauthServerType: OauthServerType): OauthMemberClient {
        return mapping[oauthServerType] ?: throw UnsupportedOauthTypeException()
    }
}
