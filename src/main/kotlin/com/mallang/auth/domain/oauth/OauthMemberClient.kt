package com.mallang.auth.domain.oauth

import com.mallang.auth.domain.Member
import com.mallang.auth.domain.OauthServerType

interface OauthMemberClient {
    fun supportServer(): OauthServerType
    fun fetch(code: String): Member
}
