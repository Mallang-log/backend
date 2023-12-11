package com.mallang.post.domain;

import static com.mallang.post.PostCategoryFixture.루트_카테고리;
import static com.mallang.post.PostCategoryFixture.하위_카테고리;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.TieredCategoryTestTemplate;
import com.mallang.post.exception.NoAuthorityPostCategoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

@DisplayName("카테고리 (PostCategory) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostCategoryTest extends TieredCategoryTestTemplate<PostCategory> {

    @Override
    protected PostCategory createRoot(String name, Member owner, Blog blog) {
        return 루트_카테고리(name, owner, blog);
    }

    @Override
    protected PostCategory createChild(String name, Member owner, Blog blog, PostCategory parent) {
        return 하위_카테고리(name, owner, blog, parent);
    }

    @Override
    protected PostCategory createChild(
            String name,
            Member owner,
            Blog blog,
            PostCategory parent,
            PostCategory prev,
            PostCategory next
    ) {
        return 하위_카테고리(name, owner, blog, parent, prev, next);
    }

    @Override
    protected Class<?> 권한_없음_예외() {
        return NoAuthorityPostCategoryException.class;
    }
}
