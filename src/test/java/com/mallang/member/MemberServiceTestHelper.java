package com.mallang.member;

import static com.mallang.member.MemberFixture.memberBuilder;
import static com.mallang.member.domain.OauthServerType.GITHUB;

import com.mallang.member.domain.MemberRepository;
import com.mallang.member.domain.OauthId;
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
        return memberRepository.save(memberBuilder()
                .oauthId(new OauthId(UUID.randomUUID().toString(), GITHUB))
                .nickname(닉네임)
                .profileImageUrl(닉네임)
                .build()
        ).getId();
    }
}
