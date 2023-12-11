package com.mallang.post.domain;

import static com.mallang.post.PostCategoryFixture.루트_카테고리;
import static com.mallang.post.PostCategoryFixture.하위_카테고리;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.category.TieredCategoryTestTemplate;
import com.mallang.post.exception.NoAuthorityPostCategoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("카테고리 (PostCategory) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostCategoryTest extends TieredCategoryTestTemplate<PostCategory> {

    @Override
    protected PostCategory createRoot(String name, Member owner) {
        return 루트_카테고리(name, owner, new Blog("blog", owner));
    }

    @Override
    protected PostCategory createChild(String name, Member owner, PostCategory parent) {
        return 하위_카테고리(name, owner, new Blog("blog", owner), parent);
    }

    @Override
    protected PostCategory createChild(
            String name,
            Member owner,
            PostCategory parent,
            PostCategory prev,
            PostCategory next
    ) {
        return 하위_카테고리(name, owner, new Blog("blog", owner), parent, prev, next);
    }

    @Override
    protected Class<?> 권한_없음_예외() {
        return NoAuthorityPostCategoryException.class;
    }

    @Nested
    class 생성_시 extends TieredCategoryTestTemplate<PostCategory>.생성_시 {

        @Test
        void 다른_사람의_블로그에_카테고리_생성_시도_시_예외() {
            // given
            Blog otherBlog = new Blog("other", otherMember);

            // when & then
            assertThatThrownBy(() -> {
                new PostCategory("root", member, otherBlog);
            }).isInstanceOf(NoAuthorityBlogException.class);
        }
    }
}
