package com.mallang.auth.application;

import com.mallang.auth.domain.oauth.AuthCodeRequestUrlProviderComposite;
import com.mallang.auth.domain.oauth.OauthMemberClientComposite;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import com.mallang.member.domain.OauthServerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final MemberRepository memberRepository;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;

    public String getAuthCodeRequestUrl(OauthServerType oauthServerType) {
        return authCodeRequestUrlProviderComposite.provide(oauthServerType);
    }

    public Long login(OauthServerType oauthServerType, String authCode) {
        Member member = oauthMemberClientComposite.fetch(oauthServerType, authCode);
        Member saved = memberRepository.findByOauthId(member.getOauthId())
                .orElseGet(() -> memberRepository.save(member));
        return saved.getId();
    }
}
