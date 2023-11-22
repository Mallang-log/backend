package com.mallang.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("블로그의 포스트 ID 생성기(PostOrderInBlogGenerator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostOrderInBlogGeneratorTest extends ServiceTest {

    @Test
    void 특정_블로그의_포스트_수보다_1큰_값을_반환한다() {
        // given
        Long mallangId = 회원을_저장한다("말랑");
        Long otherId = 회원을_저장한다("몰랑");
        Long thirdId = 회원을_저장한다("third");
        Long blogId = 블로그_개설(mallangId, "mallang-log");
        Long otherBlogId = 블로그_개설(otherId, "other-log");
        Long thirdBlogId = 블로그_개설(thirdId, "third-log");
        포스트를_저장한다(mallangId, blogId, "글1", "1");
        포스트를_저장한다(mallangId, blogId, "글2", "1");
        포스트를_저장한다(otherId, otherBlogId, "글1", "1");

        // when & then
        assertThat(postOrderInBlogGenerator.generate(blogRepository.getById(blogId))).isEqualTo(3);
        assertThat(postOrderInBlogGenerator.generate(blogRepository.getById(otherBlogId))).isEqualTo(2);
        assertThat(postOrderInBlogGenerator.generate(blogRepository.getById(thirdBlogId))).isEqualTo(1);
    }
}
