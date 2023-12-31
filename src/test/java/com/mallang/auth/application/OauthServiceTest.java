package com.mallang.auth.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.domain.OauthId.OauthServerType.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mallang.auth.domain.OauthMember;
import com.mallang.auth.domain.OauthMemberRepository;
import com.mallang.auth.domain.oauth.AuthCodeRequestUrlProviderComposite;
import com.mallang.auth.domain.oauth.OauthMemberClientComposite;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("OauthService 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class OauthServiceTest {

    private final OauthMemberRepository oauthMemberRepository = mock(OauthMemberRepository.class);
    private final OauthMemberClientComposite oauthMemberClientComposite = mock(OauthMemberClientComposite.class);
    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite =
            mock(AuthCodeRequestUrlProviderComposite.class);
    private final OauthService oauthService =
            new OauthService(oauthMemberRepository, oauthMemberClientComposite, authCodeRequestUrlProviderComposite);

    @Test
    void authCode_조회_URL_을_반환한다() {
        // given
        given(authCodeRequestUrlProviderComposite.provide(GITHUB))
                .willReturn("git");

        // when
        String authCodeRequestUrl = oauthService.getAuthCodeRequestUrl(GITHUB);

        // then
        assertThat(authCodeRequestUrl).isEqualTo("git");
    }

    @Test
    void 로그인_시_가입되지_않았다면_회원가입시킨다() {
        // given
        OauthMember 동훈 = 깃허브_동훈();
        given(oauthMemberClientComposite.fetch(GITHUB, "code"))
                .willReturn(동훈);
        given(oauthMemberRepository.findByOauthId(동훈.getOauthId()))
                .willReturn(Optional.empty());
        given(oauthMemberRepository.save(동훈))
                .willReturn(동훈);

        // when
        oauthService.login(GITHUB, "code");

        // then
        verify(oauthMemberRepository, times(1)).save(동훈);
    }

    @Test
    void 로그인_시_이미_가입되었다면_로그인만_시킨다() {
        // given
        OauthMember 동훈 = 깃허브_동훈();
        given(oauthMemberClientComposite.fetch(GITHUB, "code"))
                .willReturn(동훈);
        given(oauthMemberRepository.findByOauthId(동훈.getOauthId()))
                .willReturn(Optional.ofNullable(동훈));

        // when
        oauthService.login(GITHUB, "code");

        // then
        verify(oauthMemberRepository, times(0)).save(동훈);
    }
}
