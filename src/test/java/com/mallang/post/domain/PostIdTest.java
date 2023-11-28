package com.mallang.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 ID (PostId) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostIdTest {

    @Test
    void id와_blogId가_동일하면_같다() {
        // given
        PostId postId = new PostId(1L, 2L);
        PostId same = new PostId(1L, 2L);
        PostId other1 = new PostId(2L, 2L);
        PostId other2 = new PostId(1L, 1L);

        // when & then
        assertThat(postId)
                .hasSameHashCodeAs(same)
                .isEqualTo(same)
                .isNotEqualTo(new Object())
                .isNotEqualTo(other1)
                .isNotEqualTo(other2);
    }
}
