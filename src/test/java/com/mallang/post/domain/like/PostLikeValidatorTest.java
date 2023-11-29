package com.mallang.post.domain.like;

import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.AlreadyLikedPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 좋아요 검증기 (PostLikeValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostLikeValidatorTest {

    private final PostLikeRepository postLikeRepository = mock(PostLikeRepository.class);
    private final PostLikeValidator postLikeValidator = new PostLikeValidator(postLikeRepository);
    private final Member mallang = 깃허브_말랑(1L);
    private final Blog blog = new Blog("mallang", mallang);
    private final Post post = Post.builder()
            .blog(blog)
            .title("제목")
            .content("내용")
            .writer(mallang)
            .build();

    @Test
    void 이미_좋아요를_누른_게시물에_또다시_좋아요를_누를_수_없다() {
        // given
        given(postLikeRepository.existsByPostAndMember(post, mallang))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> {
            postLikeValidator.validateClickLike(post, mallang);
        }).isInstanceOf(AlreadyLikedPostException.class);
    }

    @Test
    void 좋아요를_누르지_않은_게시물에는_좋아요를_누를_수_있다() {
        // given
        given(postLikeRepository.existsByPostAndMember(post, mallang))
                .willReturn(false);

        // when & then
        assertDoesNotThrow(() -> {
            postLikeValidator.validateClickLike(post, mallang);
        });
    }
}
