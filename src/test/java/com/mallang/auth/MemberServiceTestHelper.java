package com.mallang.auth;

import static com.mallang.auth.domain.OauthServerType.GITHUB;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.auth.domain.OauthId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@ActiveProfiles("test")
@Component
public class MemberServiceTestHelper {

    private final MemberRepository memberRepository;

    public Long 회원을_저장한다(String 닉네임) {
        Member member = Member.builder()
                .oauthId(new OauthId(UUID.randomUUID().toString(), GITHUB))
                .nickname(닉네임)
                .profileImageUrl(닉네임)
                .build();
        return memberRepository.save(member).getId();
    }
}