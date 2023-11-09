package com.mallang.post.domain;

import static com.mallang.member.MemberFixture.말랑;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.blog.domain.Blog;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.AlreadyLikedPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 좋아요(PostLike) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostLikeTest {

    private final PostLikeValidator postLikeValidator = mock(PostLikeValidator.class);
    private final Member mallang = 말랑(1L);
    private final Blog blog = new Blog("mallang", mallang);
    private final Post post = Post.builder()
            .title("제목")
            .content("내용")
            .writer(mallang)
            .visibilityPolish(new PostVisibilityPolicy(Visibility.PUBLIC, null))
            .blog(blog)
            .build();

    @Test
    void 클릭_시_포스트의_좋아요_수가_1_증가한다() {
        // given
        PostLike postLike = new PostLike(post, mallang);

        // when
        postLike.click(postLikeValidator);

        // then
        assertThat(post.getLikeCount()).isEqualTo(1);
    }

    @Test
    void 이미_좋아요_누른_게시물에_대해서는_좋아요를_누를_수_없다() {
        // given
        PostLike postLike = new PostLike(post, mallang);
        willThrow(AlreadyLikedPostException.class)
                .given(postLikeValidator)
                .validateClickLike(post, mallang);

        // when
        assertThatThrownBy(() -> {
            postLike.click(postLikeValidator);
        }).isInstanceOf(AlreadyLikedPostException.class);

        // then
        assertThat(post.getLikeCount()).isEqualTo(0);
    }

    @Test
    void 취소_시_포스트의_좋아요_수가_1_감소한다() {
        // given
        PostLike postLike = new PostLike(post, mallang);
        postLike.click(postLikeValidator);

        // when
        postLike.cancel();

        // then
        assertThat(post.getLikeCount()).isEqualTo(0);
    }
}
