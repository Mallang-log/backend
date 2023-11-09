package com.mallang.post.domain.visibility;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.ProtectVisibilityPasswordMustRequired;
import com.mallang.post.exception.VisibilityPasswordNotRequired;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 공개범위(PostVisibilityPolicy) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostVisibilityPolicyTest {

    @Test
    void 보호가_아닌_경우_비밀번호_설정이_필요없다() {
        // when & then
        assertDoesNotThrow(() -> {
            new PostVisibilityPolicy(Visibility.PUBLIC);
        });
        assertDoesNotThrow(() -> {
            new PostVisibilityPolicy(Visibility.PRIVATE);
        });
    }

    @Test
    void 보호가_아닌_경우_비밀번호가_설정되면_예외() {
        // when & then
        assertThatThrownBy(() -> {
            new PostVisibilityPolicy(Visibility.PUBLIC, "!234");
        }).isInstanceOf(VisibilityPasswordNotRequired.class);
        assertThatThrownBy(() -> {
            new PostVisibilityPolicy(Visibility.PRIVATE, "!@34");
        }).isInstanceOf(VisibilityPasswordNotRequired.class);
    }

    @Test
    void 보호인_경우_비밀번호를_설정해야_한다() {
        // when & then
        assertDoesNotThrow(() -> {
            new PostVisibilityPolicy(Visibility.PROTECTED, "1234");
        });
    }

    @Test
    void 보호인_경우_비밀번호가_설정되지_않으면_예외() {
        // when & then
        assertThatThrownBy(() -> {
            new PostVisibilityPolicy(Visibility.PROTECTED);
        }).isInstanceOf(ProtectVisibilityPasswordMustRequired.class);
    }
}
