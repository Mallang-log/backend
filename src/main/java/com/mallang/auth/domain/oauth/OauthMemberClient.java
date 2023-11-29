package com.mallang.auth.domain.oauth;

import com.mallang.auth.domain.OauthId.OauthServerType;
import com.mallang.auth.domain.OauthMember;

public interface OauthMemberClient {

    OauthServerType supportServer();

    OauthMember fetch(String code);
}
