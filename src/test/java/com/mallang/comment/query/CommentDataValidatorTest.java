package com.mallang.comment.query;

import static com.mallang.auth.MemberFixture.말랑;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 조회 데이터 검증기 (CommentDataValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentDataValidatorTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final CommentDataValidator commentDataValidator = new CommentDataValidator(postRepository);

    private final Member owner = 말랑(10L);
    private final Blog blog = Blog.builder()
            .owner(owner)
            .name("mallang-log")
            .build();
    private final Post 공개_포스트 = Post.builder()
            .writer(owner)
            .visibilityPolish(new PostVisibilityPolicy(PUBLIC, null))
            .blog(blog)
            .build();
    private final Post 보호_포스트 = Post.builder()
            .writer(owner)
            .blog(blog)
            .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "1234"))
            .build();
    private final Post 비공개_포스트 = Post.builder()
            .writer(owner)
            .blog(blog)
            .visibilityPolish(new PostVisibilityPolicy(PRIVATE, null))
            .build();
    
    @Test
    void 공개_포스트면_문제없다() {
        // given
        given(postRepository.getById(1L))
                .willReturn(공개_포스트);

        // when & then
        assertDoesNotThrow(() -> {
            commentDataValidator.validateAccessPost(1L, null, null);
        });
    }

    @Test
    void 포스트_작성자와_조회자가_동일하면_보호든_비공개든_문제없다() {
        // given
        given(postRepository.getById(1L))
                .willReturn(비공개_포스트);
        given(postRepository.getById(2L))
                .willReturn(보호_포스트);

        // when & then
        assertDoesNotThrow(() -> {
            commentDataValidator.validateAccessPost(1L, 10L, null);
        });
        assertDoesNotThrow(() -> {
            commentDataValidator.validateAccessPost(2L, 10L, null);
        });
    }

    @Test
    void 보호_포스트인_경우_비밀번호가_일치하면_문제없다() {
        // given
        given(postRepository.getById(1L))
                .willReturn(보호_포스트);

        // when & then
        assertDoesNotThrow(() -> {
            commentDataValidator.validateAccessPost(1L, null, "1234");
        });
    }

    @Test
    void 포스트_작성자가_아니며_보호_포스트인데_비밀번호가_다르면_예외() {
        // given
        given(postRepository.getById(1L))
                .willReturn(보호_포스트);

        // when & then
        assertThatThrownBy(() -> {
            commentDataValidator.validateAccessPost(1L, 999L, "12345");
        }).isInstanceOf(NoAuthorityAccessPostException.class);
    }

    @Test
    void 포스트_작성자가_아닌데_비공개면_오류() {
        // given
        given(postRepository.getById(1L))
                .willReturn(비공개_포스트);

        // when & then
        assertThatThrownBy(() -> {
            commentDataValidator.validateAccessPost(1L, 999L, null);
        }).isInstanceOf(NoAuthorityAccessPostException.class);
    }
}
