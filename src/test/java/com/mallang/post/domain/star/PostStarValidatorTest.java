package com.mallang.post.domain.star;

import static com.mallang.auth.MemberFixture.말랑;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.AlreadyStarPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 즐겨찾기 검증기(PostStarValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostStarValidatorTest {

    private final PostStarRepository postStartRepository = mock(PostStarRepository.class);
    private final PostStarValidator postStartValidator = new PostStarValidator(postStartRepository);
    private final Member mallang = 말랑(1L);
    private final Blog blog = new Blog("mallang", mallang);
    private final Post post = Post.builder()
            .title("제목")
            .content("내용")
            .writer(mallang)
            .build();

    @Test
    void 이미_즐겨찾기를_누른_게시물에_또다시_즐겨찾기를_누를_수_없다() {
        // given
        given(postStartRepository.existsByPostAndMember(post, mallang))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> {
            postStartValidator.validateClickStar(post, mallang);
        }).isInstanceOf(AlreadyStarPostException.class);
    }

    @Test
    void 즐겨찾기를_누르지_않은_게시물에는_즐겨찾기를_누를_수_있다() {
        // given
        given(postStartRepository.existsByPostAndMember(post, mallang))
                .willReturn(false);

        // when & then
        assertDoesNotThrow(() -> {
            postStartValidator.validateClickStar(post, mallang);
        });
    }
}
