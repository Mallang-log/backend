package com.mallang.auth.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.OauthId;
import com.mallang.auth.domain.OauthId.OauthServerType;
import com.mallang.auth.domain.OauthMember;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.auth.query.response.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("회원 조회 서비스 (MemberQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class MemberQueryServiceTest {

    private final MemberQueryRepository memberQueryRepository = mock(MemberQueryRepository.class);
    private final MemberQueryService memberQueryService = new MemberQueryService(memberQueryRepository);

    @Test
    void 회원정보를_조회한다() {
        // given
        Member member = new OauthMember(
                "mallang",
                "profile",
                new OauthId("test", OauthServerType.GITHUB)
        );
        given(memberQueryRepository.getById(1L))
                .willReturn(member);

        // when
        MemberResponse memberResponse = memberQueryService.findProfile(1L);

        // then
        assertThat(memberResponse.profileImageUrl()).isEqualTo("profile");
        assertThat(memberResponse.nickname()).isEqualTo("mallang");
    }
}
