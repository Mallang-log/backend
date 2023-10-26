package com.mallang.member;

import static com.mallang.member.domain.OauthServerType.GITHUB;

import com.mallang.member.domain.Member;
import com.mallang.member.domain.OauthId;
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
        return memberBuilder()
                .id(id)
                .nickname(nickname)
                .build();
    }

    public static TestMemberBuilder memberBuilder() {
        return new TestMemberBuilder();
    }

    public static class TestMemberBuilder {
        private Long id;
        private OauthId oauthId;
        private String nickname;
        private String profileImageUrl;

        public TestMemberBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TestMemberBuilder oauthId(OauthId oauthId) {
            this.oauthId = oauthId;
            return this;
        }

        public TestMemberBuilder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public TestMemberBuilder profileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
            return this;
        }

        public Member build() {
            Member build = Member.builder()
                    .oauthId(oauthId)
                    .nickname(nickname)
                    .profileImageUrl(profileImageUrl)
                    .build();
            ReflectionTestUtils.setField(build, "id", id);
            return build;
        }
    }
}
