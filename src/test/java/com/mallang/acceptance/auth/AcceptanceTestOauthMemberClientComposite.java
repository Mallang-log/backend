package com.mallang.acceptance.auth;

import static java.util.Collections.emptySet;

import com.mallang.auth.domain.oauth.OauthMemberClientComposite;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.OauthId;
import com.mallang.member.domain.OauthServerType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Primary
@Component
public class AcceptanceTestOauthMemberClientComposite extends OauthMemberClientComposite {

    public AcceptanceTestOauthMemberClientComposite() {
        super(emptySet());
    }

    @Override
    public Member fetch(OauthServerType oauthServerType, String authCode) {
        return Member.builder()
                .oauthId(new OauthId(authCode, oauthServerType))
                .nickname(authCode)
                .profileImageUrl(authCode)
                .build();
    }
}
