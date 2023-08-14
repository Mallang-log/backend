package com.mallang.member;

import com.mallang.member.domain.Member;
import com.mallang.member.domain.OauthId;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberFixture {

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
