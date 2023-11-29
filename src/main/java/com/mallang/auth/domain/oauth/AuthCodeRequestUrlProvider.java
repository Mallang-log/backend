package com.mallang.auth.domain.oauth;

import com.mallang.auth.domain.OauthId.OauthServerType;

public interface AuthCodeRequestUrlProvider {

    OauthServerType supportServer();

    String provide();
}
