package com.mallang.auth.domain.oauth;

import static com.mallang.auth.domain.OauthId.OauthServerType.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.auth.domain.OauthId.OauthServerType;
import com.mallang.auth.exception.UnsupportedOauthTypeException;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("AuthCodeRequestUrlProviderComposite 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthCodeRequestUrlProviderCompositeTest {

    @Test
    void 등록된_AuthCodeRequestUrlProvider_중_처리가능한_Provider가_있으면_이를_사용하여_처리한다() {
        // given
        AuthCodeRequestUrlProvider provider1 = new AuthCodeRequestUrlProvider() {
            public OauthServerType supportServer() {
                return GITHUB;
            }

            public String provide() {
                return "git";
            }
        };
        AuthCodeRequestUrlProviderComposite composite = new AuthCodeRequestUrlProviderComposite(Set.of(provider1));

        // when
        String url = composite.provide(GITHUB);

        // then
        assertThat(url).isEqualTo("git");
    }

    @Test
    void 등록된_AuthCodeRequestUrlProvider_중_처리가능한_Provider가_없으면_예외() {
        // given
        AuthCodeRequestUrlProviderComposite composite = new AuthCodeRequestUrlProviderComposite(Set.of());

        // when  & then
        assertThatThrownBy(() ->
                composite.provide(GITHUB)
        ).isInstanceOf(UnsupportedOauthTypeException.class);
    }
}
