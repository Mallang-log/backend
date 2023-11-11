package com.mallang.auth.domain.oauth;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.OauthServerType;

public interface OauthMemberClient {

    OauthServerType supportServer();

    Member fetch(String code);
}
