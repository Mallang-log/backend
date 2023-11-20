package com.mallang.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.MemberServiceTestHelper;
import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.blog.domain.Blog;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.PostServiceTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("블로그의 포스트 ID 생성기(PostOrderInBlogGenerator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class PostOrderInBlogGeneratorTest {

    @Autowired
    private PostOrderInBlogGenerator postOrderInBlogGenerator;

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private BlogServiceTestHelper blogServiceTestHelper;

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Test
    void 특정_블로그의_포스트_수보다_1큰_값을_반환한다() {
        // given
        Long mallangId = memberServiceTestHelper.회원을_저장한다("말랑");
        Long otherId = memberServiceTestHelper.회원을_저장한다("몰랑");
        Long thirdId = memberServiceTestHelper.회원을_저장한다("third");
        Blog blog = blogServiceTestHelper.블로그_개설(mallangId, "mallang-log");
        Blog otherBlog = blogServiceTestHelper.블로그_개설(otherId, "other-log");
        Blog thirdBlog = blogServiceTestHelper.블로그_개설(thirdId, "third-log");
        postServiceTestHelper.포스트를_저장한다(mallangId, blog.getName(), "글1", "1");
        postServiceTestHelper.포스트를_저장한다(mallangId, blog.getName(), "글2", "1");
        postServiceTestHelper.포스트를_저장한다(otherId, otherBlog.getName(), "글1", "1");

        // when & then
        assertThat(postOrderInBlogGenerator.generate(blog)).isEqualTo(3);
        assertThat(postOrderInBlogGenerator.generate(otherBlog)).isEqualTo(2);
        assertThat(postOrderInBlogGenerator.generate(thirdBlog)).isEqualTo(1);
    }
}
