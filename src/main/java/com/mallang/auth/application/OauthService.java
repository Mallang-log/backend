package com.mallang.auth.application;

import com.mallang.auth.domain.oauth.AuthCodeRequestUrlProviderComposite;
import com.mallang.auth.domain.oauth.OauthMemberClientComposite;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import com.mallang.member.domain.OauthServerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OauthService {

    private final MemberRepository memberRepository;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;

    public String getAuthCodeRequestUrl(OauthServerType oauthServerType) {
        return authCodeRequestUrlProviderComposite.provide(oauthServerType);
    }

    public Long login(OauthServerType oauthServerType, String authCode) {
        Member member = oauthMemberClientComposite.fetch(oauthServerType, authCode);
        Member find = memberRepository.findByOauthId(member.getOauthId())
                .orElseGet(() -> signUp(member));
        return find.getId();
    }

    private Member signUp(Member member) {
        Member saved = memberRepository.save(member);
        saved.signUp();
        return memberRepository.save(saved);
    }
}
