package com.mallang.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.domain.event.MemberSignUpEvent;
import com.mallang.member.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("회원(Member) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class MemberTest {

    @Test
    void 회원가입_시_이벤트를_발행한다() {
        // given
        Member member = MemberFixture.memberBuilder().id(2L).build();

        // when
        member.signUp();

        // then
        assertThat(member.domainEvents().get(0))
                .isEqualTo(new MemberSignUpEvent(2L));
    }
}
