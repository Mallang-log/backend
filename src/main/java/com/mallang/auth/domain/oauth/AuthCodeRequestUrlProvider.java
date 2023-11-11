package com.mallang.auth.domain.oauth;

import com.mallang.auth.domain.OauthServerType;

public interface AuthCodeRequestUrlProvider {

    OauthServerType supportServer();

    String provide();
}
