package com.mallang.post.query;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.post.query.response.PostDetailResponse.WriterResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

@DisplayName("포스트 조회 데이터 검증기 (PostDataValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostDataValidatorTest {

    private final PostDataValidator postDataValidator = new PostDataValidator();

    @Nested
    class 글_접글_권한_검증_시 {

        @Test
        void 비공개_글인_경우_글의_작성자가_아닌_경우_볼_수_없다() {
            // given
            Long memberId = 1L;
            PostDetailResponse postDetailResponse = PostDetailResponse.builder()
                    .writer(new WriterResponse(memberId, "mallang", "url"))
                    .visibility(Visibility.PRIVATE)
                    .build();

            // when & then
            assertThatThrownBy(() -> {
                postDataValidator.validateAccessPost(1000L, postDetailResponse);
            });
        }

        @Test
        void 글의_주인은_비공개_글을_볼_수_있다() {
            // given
            Long memberId = 1L;
            PostDetailResponse postDetailResponse = PostDetailResponse.builder()
                    .writer(new WriterResponse(memberId, "mallang", "url"))
                    .visibility(Visibility.PRIVATE)
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                postDataValidator.validateAccessPost(memberId, postDetailResponse);
            });
        }

        @ParameterizedTest
        @EnumSource(mode = Mode.EXCLUDE, value = Visibility.class, names = {"PRIVATE"})
        void 비공개_글이_아니라면_누구나_볼_수_있다(Visibility visibility) {
            // given
            Long memberId = 1L;
            PostDetailResponse postDetailResponse = PostDetailResponse.builder()
                    .writer(new WriterResponse(memberId, "mallang", "url"))
                    .visibility(visibility)
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                postDataValidator.validateAccessPost(null, postDetailResponse);
            });
        }
    }
}
