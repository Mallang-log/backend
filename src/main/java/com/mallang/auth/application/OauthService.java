package com.mallang.auth.application;

import com.mallang.auth.domain.OauthId.OauthServerType;
import com.mallang.auth.domain.OauthMember;
import com.mallang.auth.domain.OauthMemberRepository;
import com.mallang.auth.domain.oauth.AuthCodeRequestUrlProviderComposite;
import com.mallang.auth.domain.oauth.OauthMemberClientComposite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OauthService {

    private final OauthMemberRepository oauthMemberRepository;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;

    public String getAuthCodeRequestUrl(OauthServerType oauthServerType) {
        return authCodeRequestUrlProviderComposite.provide(oauthServerType);
    }

    public Long login(OauthServerType oauthServerType, String authCode) {
        OauthMember member = oauthMemberClientComposite.fetch(oauthServerType, authCode);
        return oauthMemberRepository.findByOauthId(member.getOauthId())
                .orElseGet(() -> signup(member))
                .getId();
    }

    private OauthMember signup(OauthMember member) {
        return oauthMemberRepository.save(member);
    }
}
