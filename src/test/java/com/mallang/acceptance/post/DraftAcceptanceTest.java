package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_생성;
import static com.mallang.acceptance.post.DraftAcceptanceSteps.임시_글_단일_조회_요청;
import static com.mallang.acceptance.post.DraftAcceptanceSteps.임시_글_목록_조회_요청;
import static com.mallang.acceptance.post.DraftAcceptanceSteps.임시_글_삭제_요청;
import static com.mallang.acceptance.post.DraftAcceptanceSteps.임시_글_생성_요청;
import static com.mallang.acceptance.post.DraftAcceptanceSteps.임시_글_수정_요청;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.post.presentation.request.CreateDraftRequest;
import com.mallang.post.presentation.request.UpdateDraftRequest;
import com.mallang.post.query.response.DraftDetailResponse;
import com.mallang.post.query.response.DraftListResponse;
import io.restassured.common.mapper.TypeRef;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

;

@DisplayName("임시 글 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class DraftAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private String 말랑_블로그_이름;
    private Long Spring_카테고리_ID;
    private CreateDraftRequest 임시_글_생성_요청;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        말랑_블로그_이름 = 블로그_개설(말랑_세션_ID, "mallang-log");
        Spring_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_이름, "Spring", 없음());
        임시_글_생성_요청 = new CreateDraftRequest(
                말랑_블로그_이름,
                "첫 임시_글",
                "첫 임시_글이네요.",
                "임시_글 썸네일 이름",
                "첫 임시_글 인트로",
                Spring_카테고리_ID,
                List.of("태그1", "태그2")
        );
    }

    @Nested
    class 임시_글_저장_API {

        @Test
        void 임시_글를_작성한다() {
            // when
            var 응답 = 임시_글_생성_요청(말랑_세션_ID, 임시_글_생성_요청);

            // then
            응답_상태를_검증한다(응답, 생성됨);
        }

        @Test
        void 내_블로그가_아니면_예외() {
            // given
            CreateDraftRequest createDraftRequest = new CreateDraftRequest(
                    말랑_블로그_이름,
                    "첫 임시_글",
                    "첫 임시_글이네요.",
                    "임시_글 썸네일 이름",
                    "첫 임시_글 인트로",
                    Spring_카테고리_ID,
                    List.of("태그1", "태그2")
            );

            // when
            var 응답 = 임시_글_생성_요청(동훈_세션_ID, createDraftRequest);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 임시_글_수정_API {

        @Test
        void 임시_글를_업데이트한다() {
            // given
            var 임시_글_ID = ID를_추출한다(임시_글_생성_요청(말랑_세션_ID, 임시_글_생성_요청));
            UpdateDraftRequest 임시_글_수정_요청 = new UpdateDraftRequest(
                    "업데이트 제목",
                    "업데이트 내용",
                    "업데이트 임시_글 썸네일 이름",
                    "업데이트 인트로",
                    Spring_카테고리_ID,
                    List.of("태그1", "태그2")
            );

            // when
            var 응답 = 임시_글_수정_요청(말랑_세션_ID, 임시_글_ID, 임시_글_수정_요청);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 내가_작성한_글이_아니면_예외() {
            // given
            var 임시_글_ID = ID를_추출한다(임시_글_생성_요청(말랑_세션_ID, 임시_글_생성_요청));
            UpdateDraftRequest 임시_글_수정_요청 = new UpdateDraftRequest(
                    "업데이트 제목",
                    "업데이트 내용",
                    "업데이트 임시_글 썸네일 이름",
                    "업데이트 인트로",
                    Spring_카테고리_ID,
                    List.of("태그1", "태그2")
            );

            // when
            var 응답 = 임시_글_수정_요청(동훈_세션_ID, 임시_글_ID, 임시_글_수정_요청);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 임시_글_삭제_API {

        @Test
        void 임시_글를_삭제한다() {
            // given
            var 임시_글_ID = ID를_추출한다(임시_글_생성_요청(말랑_세션_ID, 임시_글_생성_요청));

            // when
            var 응답 = 임시_글_삭제_요청(말랑_세션_ID, 임시_글_ID);

            // then
            응답_상태를_검증한다(응답, 본문_없음);
        }

        @Test
        void 내가_작성한_글이_아니면_예외() {
            // given
            var 임시_글_ID = ID를_추출한다(임시_글_생성_요청(말랑_세션_ID, 임시_글_생성_요청));

            // when
            var 응답 = 임시_글_삭제_요청(동훈_세션_ID, 임시_글_ID);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 임시_글_목록_조회_API {

        @Test
        void 블로그의_임시_글_목록을_조회한다() {
            // given
            CreateDraftRequest 임시_글_1_생성_요청 = new CreateDraftRequest(
                    말랑_블로그_이름,
                    "임시 글 1",
                    "첫 임시 글이네요.",
                    "임시 글 썸네일 이름",
                    "첫 임시 글 인트로",
                    Spring_카테고리_ID,
                    List.of("태그1", "태그2")
            );
            CreateDraftRequest 임시_글_2_생성_요청 = new CreateDraftRequest(
                    말랑_블로그_이름,
                    "임시 글 2",
                    "두번째",
                    "임시 글 썸네일 이름",
                    "임시 글 2 인트로",
                    null,
                    Collections.emptyList()
            );
            임시_글_생성_요청(말랑_세션_ID, 임시_글_1_생성_요청);
            임시_글_생성_요청(말랑_세션_ID, 임시_글_2_생성_요청);

            // when
            var 응답 = 임시_글_목록_조회_요청(말랑_세션_ID, 말랑_블로그_이름);

            // then
            List<DraftListResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses)
                    .extracting(DraftListResponse::title)
                    .containsExactly("임시 글 2", "임시 글 1");
        }

        @Test
        void 블로그_주인이_아닌_경우_예외() {
            // when
            var 응답 = 임시_글_목록_조회_요청(동훈_세션_ID, 말랑_블로그_이름);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 임시_글_불러오기_API {

        @Test
        void 임시_글을_조회한다() {
            // given
            var 임시_글_ID = ID를_추출한다(임시_글_생성_요청(말랑_세션_ID, 임시_글_생성_요청));

            // when
            var 응답 = 임시_글_단일_조회_요청(말랑_세션_ID, 임시_글_ID);

            // then
            DraftDetailResponse draftDetailResponse = 응답.as(DraftDetailResponse.class);
            assertThat(draftDetailResponse.title())
                    .isEqualTo("첫 임시_글");
        }

        @Test
        void 임시_글_작성자가_아니면_예외() {
            // given
            var 임시_글_ID = ID를_추출한다(임시_글_생성_요청(말랑_세션_ID, 임시_글_생성_요청));

            // when
            var 응답 = 임시_글_단일_조회_요청(동훈_세션_ID, 임시_글_ID);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 없는_글_조회_시_예외() {
            // when
            var 응답 = 임시_글_단일_조회_요청(말랑_세션_ID, 100L);

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
        }
    }
}
