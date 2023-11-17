package com.mallang.auth.domain.oauth

import com.mallang.auth.domain.OauthServerType

interface AuthCodeRequestUrlProvider {
    fun supportServer(): OauthServerType
    fun provide(): String
}
