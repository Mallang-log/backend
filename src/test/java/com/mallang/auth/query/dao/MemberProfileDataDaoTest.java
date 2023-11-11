package com.mallang.auth.query.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.auth.domain.OauthId;
import com.mallang.auth.domain.OauthServerType;
import com.mallang.auth.query.data.MemberProfileData;
import com.mallang.common.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("회원 프로필 조회 DAO(MemberProfileDataDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class MemberProfileDataDaoTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberProfileDataDao memberProfileDataDao;

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
        MemberProfileData memberProfileData = memberProfileDataDao.find(saved.getId());

        // then
        assertThat(memberProfileData.id()).isEqualTo(saved.getId());
        assertThat(memberProfileData.profileImageUrl()).isEqualTo("profile");
        assertThat(memberProfileData.nickname()).isEqualTo("mallang");
    }
}
