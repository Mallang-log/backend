package com.mallang.auth.query.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.auth.domain.OauthId;
import com.mallang.auth.domain.OauthServerType;
import com.mallang.auth.query.response.MemberResponse;
import com.mallang.common.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("회원 조회 DAO(MemberDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class MemberDaoTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberDao memberDao;

    @Test
    void 회원정보를_조회한다() {
        // given
        Member member = Member.builder()
                .nickname("mallang")
                .profileImageUrl("profile")
                .oauthId(new OauthId("test", OauthServerType.GITHUB))
                .build();
        Member saved = memberRepository.save(member);

        // when
        MemberResponse memberResponse = memberDao.find(saved.getId());

        // then
        assertThat(memberResponse.id()).isEqualTo(saved.getId());
        assertThat(memberResponse.profileImageUrl()).isEqualTo("profile");
        assertThat(memberResponse.nickname()).isEqualTo("mallang");
    }
}
