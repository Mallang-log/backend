package com.mallang.auth.domain.oauth;

import com.mallang.member.domain.OauthServerType;

public interface AuthCodeRequestUrlProvider {

    OauthServerType supportServer();

    String provide();
}
