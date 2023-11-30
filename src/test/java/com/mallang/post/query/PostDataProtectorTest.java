package com.mallang.post.query;

import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.post.query.response.PostSearchResponse;
import com.mallang.post.query.response.PostSearchResponse.WriterResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("포스트 조회 데이터 보호기 (PostDataProtector) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostDataProtectorTest {

    private final Pageable pageable = PageRequest.of(0, 100);
    private final PostDataProtector postDataProtector = new PostDataProtector();

    @Nested
    class 단일_글_조회_시 {

        @Test
        void 보호_글이라도_글의_주인이_조회한다면_내용을_다_보여준다() {
            // given
            Long memberId = 1L;
            PostDetailResponse postDetailResponse = PostDetailResponse.builder()
                    .writer(new PostDetailResponse.WriterResponse(memberId, "mallang", "url"))
                    .bodyText("bodyText")
                    .visibility(PROTECTED)
                    .password("1234")
                    .build();

            // when
            PostDetailResponse protectedData = postDataProtector.protectIfRequired(memberId, null, postDetailResponse);

            // then
            assertThat(protectedData.bodyText()).isEqualTo("bodyText");
        }

        @Test
        void 보호글의_경우_비밀번호가_달라도_주인이라면_볼_수_있다() {
            // given
            Long memberId = 1L;
            PostDetailResponse postDetailResponse = PostDetailResponse.builder()
                    .writer(new PostDetailResponse.WriterResponse(memberId, "mallang", "url"))
                    .bodyText("bodyText")
                    .visibility(PROTECTED)
                    .password("1234")
                    .build();

            // when
            PostDetailResponse protectedData = postDataProtector.protectIfRequired(memberId, "12345",
                    postDetailResponse);

            // then
            assertThat(protectedData.bodyText()).isEqualTo("bodyText");
        }

        @Test
        void 보호글의_경우_글의_주인이_아닌_경우_비밀번호가_일치하면_볼_수_있다() {
            // given
            Long memberId = 1L;
            PostDetailResponse postDetailResponse = PostDetailResponse.builder()
                    .writer(new PostDetailResponse.WriterResponse(memberId, "mallang", "url"))
                    .bodyText("bodyText")
                    .postThumbnailImageName("thumb")
                    .visibility(PROTECTED)
                    .password("1234")
                    .build();

            // when
            PostDetailResponse protectedData = postDataProtector.protectIfRequired(memberId + 1, "1234",
                    postDetailResponse);

            // then
            assertThat(protectedData.bodyText()).isEqualTo("bodyText");
        }

        @Test
        void 보호글의_경우_글의_주인이_아니며_비밀번호가_다르다면_보호된다() {
            // given
            Long memberId = 1L;
            PostDetailResponse postDetailResponse = PostDetailResponse.builder()
                    .writer(new PostDetailResponse.WriterResponse(memberId, "mallang", "url"))
                    .bodyText("bodyText")
                    .postThumbnailImageName("thumb")
                    .visibility(PROTECTED)
                    .build();

            // when
            PostDetailResponse protectedData = postDataProtector.protectIfRequired(memberId + 1, null,
                    postDetailResponse);

            // then
            assertThat(protectedData.bodyText()).isEqualTo("보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.");
            assertThat(protectedData.postThumbnailImageName()).isEmpty();
        }

        @ParameterizedTest
        @EnumSource(mode = Mode.EXCLUDE, value = Visibility.class, names = {"PROTECTED"})
        void 공개_글은_모두에게_전체_공개될_수_있다(Visibility visibility) {
            // given
            Long memberId = 1L;
            PostDetailResponse postDetailResponse = PostDetailResponse.builder()
                    .writer(new PostDetailResponse.WriterResponse(memberId, "mallang", "url"))
                    .bodyText("bodyText")
                    .visibility(visibility)
                    .build();

            // when
            PostDetailResponse protectedData = postDataProtector.protectIfRequired(null, null, postDetailResponse);

            // then
            assertThat(protectedData.bodyText()).isEqualTo("bodyText");
        }
    }

    @Nested
    class 글_검색_시 {

        @Test
        void 검색_결과_중_보호된_글은_주인만_볼_수_있으며_주인이_아니라면_보호되어_반환된다() {
            // given
            Page<PostSearchResponse> result = new PageImpl<>(
                    List.of(
                            PostSearchResponse.builder()
                                    .writer(new WriterResponse(1L, "mallang", "url"))
                                    .title("mallang-public")
                                    .bodyText("mallang-public")
                                    .postThumbnailImageName("thumb-mallang-public")
                                    .intro("intro")
                                    .visibility(PUBLIC)
                                    .build(),
                            PostSearchResponse.builder()
                                    .writer(new WriterResponse(1L, "mallang", "url"))
                                    .title("mallang-protected")
                                    .bodyText("mallang-protected")
                                    .postThumbnailImageName("thumb-mallang-protected")
                                    .intro("intro")
                                    .visibility(PROTECTED)
                                    .build(),
                            PostSearchResponse.builder()
                                    .writer(new WriterResponse(1L, "mallang", "url"))
                                    .title("mallang-private")
                                    .bodyText("mallang-private")
                                    .postThumbnailImageName("thumb-mallang-private")
                                    .intro("intro")
                                    .visibility(PRIVATE)
                                    .build(),

                            PostSearchResponse.builder()
                                    .writer(new WriterResponse(2L, "other", "url"))
                                    .title("other-public")
                                    .bodyText("other-public")
                                    .postThumbnailImageName("thumb-other-public")
                                    .intro("intro")
                                    .visibility(PUBLIC)
                                    .build(),
                            PostSearchResponse.builder()
                                    .writer(new WriterResponse(2L, "other", "url"))
                                    .title("other-protected")
                                    .bodyText("other-protected")
                                    .postThumbnailImageName("thumb-other-protected")
                                    .intro("intro")
                                    .visibility(PROTECTED)
                                    .build()),
                    pageable,
                    5
            );

            // when
            Page<PostSearchResponse> protect = postDataProtector.protectIfRequired(1L, result);

            // then
            List<PostSearchResponse> expected = List.of(
                    PostSearchResponse.builder()
                            .writer(new WriterResponse(1L, "mallang", "url"))
                            .title("mallang-public")
                            .bodyText("mallang-public")
                            .postThumbnailImageName("thumb-mallang-public")
                            .intro("intro")
                            .visibility(PUBLIC)
                            .build(),
                    PostSearchResponse.builder()
                            .writer(new WriterResponse(1L, "mallang", "url"))
                            .title("mallang-protected")
                            .bodyText("mallang-protected")
                            .postThumbnailImageName("thumb-mallang-protected")
                            .intro("intro")
                            .visibility(PROTECTED)
                            .build(),
                    PostSearchResponse.builder()
                            .writer(new WriterResponse(1L, "mallang", "url"))
                            .title("mallang-private")
                            .bodyText("mallang-private")
                            .postThumbnailImageName("thumb-mallang-private")
                            .intro("intro")
                            .visibility(PRIVATE)
                            .build(),

                    PostSearchResponse.builder()
                            .writer(new WriterResponse(2L, "other", "url"))
                            .title("other-public")
                            .bodyText("other-public")
                            .postThumbnailImageName("thumb-other-public")
                            .intro("intro")
                            .visibility(PUBLIC)
                            .build(),
                    PostSearchResponse.builder()
                            .writer(new WriterResponse(2L, "other", "url"))
                            .title("other-protected")
                            .bodyText("보호되어 있는 글입니다.")
                            .postThumbnailImageName("")
                            .intro("")
                            .visibility(PROTECTED)
                            .build());
            assertThat(protect.getContent())
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        }
    }
}
