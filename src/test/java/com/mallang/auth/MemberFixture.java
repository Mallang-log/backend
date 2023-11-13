package com.mallang.auth;

import static com.mallang.auth.domain.OauthServerType.GITHUB;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.OauthId;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberFixture {

    public static Member 말랑() {
        return 말랑(null);
    }

    public static Member 말랑(Long id) {
        Member member = Member.builder()
                .oauthId(new OauthId("mallang", GITHUB))
                .nickname("말랑")
                .profileImageUrl("https://avatars.githubusercontent.com/u/mallang")
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    public static Member 동훈() {
        return 동훈(null);
    }


    public static Member 동훈(Long id) {
        Member member = Member.builder()
                .oauthId(new OauthId("donghun", GITHUB))
                .nickname("동훈")
                .profileImageUrl("https://avatars.githubusercontent.com/u/donghun")
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    public static Member 회원(String nickname) {
        return 회원(null, nickname);
    }

    public static Member 회원(Long id, String nickname) {
        Member build = Member.builder()
                .nickname(nickname)
                .build();
        ReflectionTestUtils.setField(build, "id", id);
        return build;
    }
}
