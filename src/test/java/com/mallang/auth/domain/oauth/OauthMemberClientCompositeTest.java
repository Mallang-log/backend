package com.mallang.auth.domain.oauth;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.domain.OauthId.OauthServerType.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.auth.domain.OauthId.OauthServerType;
import com.mallang.auth.domain.OauthMember;
import com.mallang.auth.exception.UnsupportedOauthTypeException;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("OauthMemberClientComposite 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class OauthMemberClientCompositeTest {

    @Test
    void 등록된_OauthMemberClient_중_처리가능한_Client_가_있으면_이를_사용하여_처리한다() {
        // given
        OauthMemberClient provider1 = new OauthMemberClient() {
            @Override
            public OauthServerType supportServer() {
                return GITHUB;
            }

            @Override
            public OauthMember fetch(String code) {
                return 깃허브_동훈();
            }
        };
        OauthMemberClientComposite composite = new OauthMemberClientComposite(Set.of(provider1));

        // when
        OauthMember member = composite.fetch(GITHUB, "test");

        // then
        assertThat(member.getNickname()).isEqualTo("동훈");
    }

    @Test
    void 등록된_OauthMemberClient_중_처리가능한_Client가_없으면_예외() {
        // given
        OauthMemberClientComposite composite = new OauthMemberClientComposite(Set.of());

        // when  & then
        assertThatThrownBy(() ->
                composite.fetch(GITHUB, "test")
        ).isInstanceOf(UnsupportedOauthTypeException.class);
    }
}
