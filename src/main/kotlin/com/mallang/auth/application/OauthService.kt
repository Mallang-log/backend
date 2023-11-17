package com.mallang.auth.application

import com.mallang.auth.domain.Member
import com.mallang.auth.domain.MemberRepository
import com.mallang.auth.domain.OauthServerType
import com.mallang.auth.domain.oauth.AuthCodeRequestUrlProviderComposite
import com.mallang.auth.domain.oauth.OauthMemberClientComposite
import org.springframework.stereotype.Service

@Service
class OauthService(
        private val memberRepository: MemberRepository,
        private val oauthMemberClientComposite: OauthMemberClientComposite,
        private val authCodeRequestUrlProviderComposite: AuthCodeRequestUrlProviderComposite
) {

    fun getAuthCodeRequestUrl(oauthServerType: OauthServerType): String {
        return authCodeRequestUrlProviderComposite.provide(oauthServerType)
    }

    fun login(oauthServerType: OauthServerType, authCode: String): Long {
        val member = oauthMemberClientComposite.fetch(oauthServerType, authCode)
        return memberRepository.findByOauthId(member.oauthId)
                .orElseGet { signUp(member) }
                .id
    }

    private fun signUp(member: Member): Member {
        return memberRepository.save(member)
    }
}
