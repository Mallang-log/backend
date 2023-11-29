package com.mallang.auth;


import static com.mallang.auth.domain.OauthId.OauthServerType.GITHUB;

import com.mallang.auth.domain.OauthId;
import com.mallang.auth.domain.OauthMember;
import org.springframework.test.util.ReflectionTestUtils;

public class OauthMemberFixture {

    public static OauthMember 깃허브_말랑() {
        return 깃허브_말랑(null);
    }

    public static OauthMember 깃허브_말랑(Long id) {
        OauthMember member = OauthMember.builder()
                .oauthId(new OauthId("mallang", GITHUB))
                .nickname("말랑")
                .profileImageUrl("https://avatars.githubusercontent.com/u/mallang")
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    public static OauthMember 깃허브_동훈() {
        return 깃허브_동훈(null);
    }

    public static OauthMember 깃허브_동훈(Long id) {
        OauthMember member = OauthMember.builder()
                .oauthId(new OauthId("donghun", GITHUB))
                .nickname("동훈")
                .profileImageUrl("https://avatars.githubusercontent.com/u/donghun")
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    public static OauthMember 깃허브_회원(String nickname) {
        return 깃허브_회원(null, nickname);
    }

    public static OauthMember 깃허브_회원(Long id, String nickname) {
        OauthMember build = OauthMember.builder()
                .nickname(nickname)
                .build();
        ReflectionTestUtils.setField(build, "id", id);
        return build;
    }
}
