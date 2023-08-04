package com.mallang.auth.domain.oauth;

import com.mallang.member.domain.Member;
import com.mallang.member.domain.OauthServerType;

public interface OauthMemberClient {

    OauthServerType supportServer();

    Member fetch(String code);
}
