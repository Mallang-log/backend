package com.mallang.post.query;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.IncorrectAccessPostException;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import com.mallang.post.query.data.PostDetailData;
import com.mallang.post.query.data.PostDetailData.WriterDetailInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

@DisplayName("포스트 조회 데이터 검증기(PostDataValidator) 은(는)")
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
            PostDetailData postDetailData = PostDetailData.builder()
                    .writerInfo(new WriterDetailInfo(memberId, "mallang", "url"))
                    .visibility(Visibility.PRIVATE)
                    .build();

            // when & then
            assertThatThrownBy(() -> {
                postDataValidator.validateAccessPost(1000L, postDetailData);
            });
        }

        @Test
        void 글의_주인은_비공개_글을_볼_수_있다() {
            // given
            Long memberId = 1L;
            PostDetailData postDetailData = PostDetailData.builder()
                    .writerInfo(new WriterDetailInfo(memberId, "mallang", "url"))
                    .visibility(Visibility.PRIVATE)
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                postDataValidator.validateAccessPost(memberId, postDetailData);
            });
        }

        @ParameterizedTest
        @EnumSource(mode = Mode.EXCLUDE, value = Visibility.class, names = {"PRIVATE"})
        void 비공개_글이_아니라면_누구나_볼_수_있다(Visibility visibility) {
            // given
            Long memberId = 1L;
            PostDetailData postDetailData = PostDetailData.builder()
                    .writerInfo(new WriterDetailInfo(memberId, "mallang", "url"))
                    .visibility(visibility)
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                postDataValidator.validateAccessPost(null, postDetailData);
            });
        }
    }

    @Nested
    class 보호_글_접근_검증_시 {

        @ParameterizedTest
        @EnumSource(mode = Mode.EXCLUDE, value = Visibility.class, names = {"PROTECTED"})
        void 보호_글이_아니면_예외(Visibility visibility) {
            // given
            PostDetailData postDetailData = PostDetailData.builder()
                    .writerInfo(new WriterDetailInfo(1L, "mallang", "url"))
                    .visibility(visibility)
                    .build();

            // when & then
            assertThatThrownBy(() -> {
                postDataValidator.validateAccessProtectedPost(postDetailData, "11234");
            }).isInstanceOf(IncorrectAccessPostException.class);
        }

        @Test
        void 비밀번호가_일치하지_않으면_예외() {
            // given
            PostDetailData postDetailData = PostDetailData.builder()
                    .writerInfo(new WriterDetailInfo(1L, "mallang", "url"))
                    .visibility(Visibility.PROTECTED)
                    .password("1234")
                    .build();

            // when & then
            assertThatThrownBy(() -> {
                postDataValidator.validateAccessProtectedPost(postDetailData, "11234");
            }).isInstanceOf(NoAuthorityAccessPostException.class);
        }

        @Test
        void 비밀번호가_일치하면_성공() {
            // given
            PostDetailData postDetailData = PostDetailData.builder()
                    .writerInfo(new WriterDetailInfo(1L, "mallang", "url"))
                    .visibility(Visibility.PROTECTED)
                    .password("1234")
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                postDataValidator.validateAccessProtectedPost(postDetailData, "1234");
            });
        }
    }
}
