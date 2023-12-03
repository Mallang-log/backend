package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.인증되지_않음;
import static com.mallang.acceptance.AcceptanceSteps.중복됨;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.내_정보_조회_요청;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.공개_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.보호_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.비공개_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_수정_요청;
import static com.mallang.acceptance.post.PostStarAcceptanceSteps.특정_회원의_즐겨찾기_포스트_목록_조회_요청;
import static com.mallang.acceptance.post.PostStarAcceptanceSteps.포스트_즐겨찾기_요청;
import static com.mallang.acceptance.post.PostStarAcceptanceSteps.포스트_즐겨찾기_취소_요청;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.auth.query.response.MemberResponse;
import com.mallang.common.presentation.PageResponse;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.post.presentation.request.UpdatePostRequest;
import com.mallang.post.query.response.StaredPostResponse;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 즐겨찾기 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostStarAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private Long 동훈_ID;
    private String 블로그_이름;
    private UpdatePostRequest 공개_포스트를_보호로_바꾸는_요청;
    private UpdatePostRequest 공개_포스트를_비공개로_바꾸는_요청;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        블로그_이름 = 블로그_개설(말랑_세션_ID, "mallang-log");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        동훈_ID = 내_정보_조회_요청(동훈_세션_ID).as(MemberResponse.class).id();
        공개_포스트를_보호로_바꾸는_요청 = new UpdatePostRequest(
                블로그_이름,
                "보호로 변경",
                "인트로", "공개글에서 보호됨",
                null,
                PROTECTED,
                "1234",
                null,
                emptyList()
        );
        공개_포스트를_비공개로_바꾸는_요청 = new UpdatePostRequest(
                블로그_이름,
                "보호로 변경",
                "인트로", "공개글에서 비공개됨",
                null,
                PRIVATE,
                null,
                null,
                emptyList()
        );
    }

    @Nested
    class 즐겨찾기_API {

        @Test
        void 로그인하지_않았다면_즐겨찾기를_누를_수_없다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));

            // when
            var 응답 = 포스트_즐겨찾기_요청(없음(), 포스트_ID, 블로그_이름, null);

            // then
            응답_상태를_검증한다(응답, 인증되지_않음);
        }

        @Test
        void 포스트에_즐겨찾기를_누른다() {
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));

            // when
            var 응답 = 포스트_즐겨찾기_요청(동훈_세션_ID, 포스트_ID, 블로그_이름, null);

            // then
            응답_상태를_검증한다(응답, 생성됨);
        }

        @Test
        void 이미_즐겨찾기를_누른_포스트에는_중복해서_즐겨찾기를_누를_수_없다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));
            포스트_즐겨찾기_요청(말랑_세션_ID, 포스트_ID, 블로그_이름, null);

            // when
            var 응답 = 포스트_즐겨찾기_요청(말랑_세션_ID, 포스트_ID, 블로그_이름, null);

            // then
            응답_상태를_검증한다(응답, 중복됨);
        }

        @Nested
        class 보호된_포스트인_경우 {

            @Nested
            class 블로그_주인인_경우 {

                @Test
                void 즐겨찾기를_누를_수_있다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(블로그_이름));

                    // when
                    var 응답 = 포스트_즐겨찾기_요청(말랑_세션_ID, 포스트_ID, 블로그_이름, null);

                    // then
                    응답_상태를_검증한다(응답, 생성됨);
                }
            }

            @Nested
            class 블로그_주인이_아닌_경우 {

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_일치하지_않으면_즐겨찾기를_누를_수_없다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(블로그_이름));

                    // when
                    var 응답 = 포스트_즐겨찾기_요청(동훈_세션_ID, 포스트_ID, 블로그_이름, null);

                    // then
                    응답_상태를_검증한다(응답, 권한_없음);
                }

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_일치하면_즐겨찾기를_누를_수_있다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(블로그_이름));

                    // when
                    var 응답 = 포스트_즐겨찾기_요청(동훈_세션_ID, 포스트_ID, 블로그_이름, "1234");

                    // then
                    응답_상태를_검증한다(응답, 생성됨);
                }
            }
        }

        @Nested
        class 비공개_포스트인_경우 {

            @Test
            void 블로그_주인은_누를_수_있다() {
                // given
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 비공개_포스트_생성_데이터(블로그_이름));

                // when
                var 응답 = 포스트_즐겨찾기_요청(말랑_세션_ID, 포스트_ID, 블로그_이름, null);

                // then
                응답_상태를_검증한다(응답, 생성됨);
            }

            @Test
            void 블로그_주인이_아닌_경우_누를_수_없다() {
                // given
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 비공개_포스트_생성_데이터(블로그_이름));

                // when
                var 응답 = 포스트_즐겨찾기_요청(동훈_세션_ID, 포스트_ID, 블로그_이름, null);

                // then
                응답_상태를_검증한다(응답, 권한_없음);
            }
        }
    }

    @Nested
    class 즐겨찾기_취소_API {

        @Test
        void 즐겨찾기를_취소한다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));
            포스트_즐겨찾기_요청(말랑_세션_ID, 포스트_ID, 블로그_이름, null);

            // when
            var 응답 = 포스트_즐겨찾기_취소_요청(말랑_세션_ID, 포스트_ID, 블로그_이름);

            // then
            응답_상태를_검증한다(응답, 본문_없음);
        }

        @Test
        void 즐겨찾기를_누르지_않은_경우_취소하면_예외가_발생한다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));

            // when
            var 응답 = 포스트_즐겨찾기_취소_요청(말랑_세션_ID, 포스트_ID, 블로그_이름);

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
        }

        @Test
        void 보호된_글의_경우에도_취소할_수_있다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));
            var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트_ID, 블로그_이름, null);
            포스트_수정_요청(말랑_세션_ID, 포스트_ID, 공개_포스트를_비공개로_바꾸는_요청);

            // when
            var 응답 = 포스트_즐겨찾기_취소_요청(동훈_세션_ID, 포스트_ID, 블로그_이름);

            // then
            응답_상태를_검증한다(응답, 본문_없음);
        }

        @Test
        void 비공개된_글의_경우에도_취소할_수_있다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));
            var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트_ID, 블로그_이름, null);
            포스트_수정_요청(말랑_세션_ID, 포스트_ID, 공개_포스트를_비공개로_바꾸는_요청);

            // when
            var 응답 = 포스트_즐겨찾기_취소_요청(동훈_세션_ID, 포스트_ID, 블로그_이름);

            // then
            응답_상태를_검증한다(응답, 본문_없음);
        }
    }

    @Nested
    class 특정_회원의_즐겨찾기_목록_조회_API {

        private CreatePostRequest 포스트1_데이터;
        private CreatePostRequest 포스트2_데이터;
        private CreatePostRequest 포스트3_데이터;

        @BeforeEach
        protected void setUp() {
            포스트1_데이터 = new CreatePostRequest(
                    블로그_이름,
                    "포스트1",
                    "12345", "내용1",
                    null,
                    PUBLIC,
                    null,
                    null,
                    null
            );
            포스트2_데이터 = new CreatePostRequest(
                    블로그_이름,
                    "포스트2",
                    "12345", "내용2",
                    null,
                    PUBLIC,
                    null,
                    null,
                    null
            );
            포스트3_데이터 = new CreatePostRequest(
                    블로그_이름,
                    "포스트3",
                    "12345", "내용3",
                    null,
                    PUBLIC,
                    null,
                    null,
                    null
            );
        }

        @Test
        void 누구나_볼_수_있다() {
            // given
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트1_데이터);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트2_데이터);
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 포스트3_데이터);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트1_ID, 블로그_이름, null);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트2_ID, 블로그_이름, null);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트3_ID, 블로그_이름, null);

            // when
            var 응답 = 특정_회원의_즐겨찾기_포스트_목록_조회_요청(null, 동훈_ID);

            // then
            PageResponse<StaredPostResponse> result = 응답.as(new TypeRef<>() {
            });
            assertThat(result.content())
                    .extracting(StaredPostResponse::title)
                    .containsExactly("포스트3", "포스트2", "포스트1");
        }

        @Test
        void 보호_글은_글_작성자가_조회하지_않는_이상_보호되어_조회된다() {
            // given
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트1_데이터);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트2_데이터);
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 포스트3_데이터);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트1_ID, 블로그_이름, null);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트2_ID, 블로그_이름, null);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트3_ID, 블로그_이름, null);

            포스트_수정_요청(말랑_세션_ID, 포스트1_ID, 공개_포스트를_보호로_바꾸는_요청);

            // when
            var 응답 = 특정_회원의_즐겨찾기_포스트_목록_조회_요청(동훈_세션_ID, 동훈_ID);

            // then
            PageResponse<StaredPostResponse> result = 응답.as(new TypeRef<>() {
            });
            assertThat(result.content())
                    .extracting(StaredPostResponse::bodyText)
                    .containsExactly("내용3", "내용2", "보호되어 있는 글입니다.");
        }

        @Test
        void 글_작성자가_다른_회원의_즐겨찾기_목록_조회_시_글_작성자의_보호글이_즐겨찾이_되어있다면_볼_수_있다() {
            // given
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트1_데이터);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트2_데이터);
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 포스트3_데이터);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트1_ID, 블로그_이름, null);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트2_ID, 블로그_이름, null);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트3_ID, 블로그_이름, null);

            포스트_수정_요청(말랑_세션_ID, 포스트1_ID, 공개_포스트를_보호로_바꾸는_요청);

            // when
            var 응답 = 특정_회원의_즐겨찾기_포스트_목록_조회_요청(말랑_세션_ID, 동훈_ID);

            // then
            PageResponse<StaredPostResponse> result = 응답.as(new TypeRef<>() {
            });
            assertThat(result.content())
                    .extracting(StaredPostResponse::bodyText)
                    .containsExactly("내용3", "내용2", "공개글에서 보호됨");
        }

        @Test
        void 비공개_글은_조회되지_않는다() {
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트1_데이터);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트2_데이터);
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 포스트3_데이터);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트1_ID, 블로그_이름, null);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트2_ID, 블로그_이름, null);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트3_ID, 블로그_이름, null);

            포스트_수정_요청(말랑_세션_ID, 포스트1_ID, 공개_포스트를_비공개로_바꾸는_요청);

            // when
            var 응답 = 특정_회원의_즐겨찾기_포스트_목록_조회_요청(말랑_세션_ID, 동훈_ID);

            // then
            PageResponse<StaredPostResponse> result = 응답.as(new TypeRef<>() {
            });
            assertThat(result.content())
                    .extracting(StaredPostResponse::bodyText)
                    .containsExactly("내용3", "내용2");
        }

        @Test
        void 비공개_글이었다_다시_보호나_공개_상태로_변환되면_조회된다() {
            // given
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트1_데이터);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트2_데이터);
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 포스트3_데이터);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트1_ID, 블로그_이름, null);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트2_ID, 블로그_이름, null);
            포스트_즐겨찾기_요청(동훈_세션_ID, 포스트3_ID, 블로그_이름, null);
            포스트_수정_요청(말랑_세션_ID, 포스트1_ID, 공개_포스트를_비공개로_바꾸는_요청);
            포스트_수정_요청(말랑_세션_ID, 포스트1_ID, 공개_포스트를_보호로_바꾸는_요청);

            // when
            var 응답 = 특정_회원의_즐겨찾기_포스트_목록_조회_요청(말랑_세션_ID, 동훈_ID);

            // then
            PageResponse<StaredPostResponse> result = 응답.as(new TypeRef<>() {
            });
            assertThat(result.content())
                    .extracting(StaredPostResponse::bodyText)
                    .containsExactly("내용3", "내용2", "공개글에서 보호됨");
        }
    }
}
