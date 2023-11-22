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
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.post.PostLikeAcceptanceSteps.좋아요_취소_요청;
import static com.mallang.acceptance.post.PostLikeAcceptanceSteps.포스트_좋아요_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.공개_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.보호_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.비공개_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_수정_요청;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static java.util.Collections.emptyList;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.post.presentation.request.UpdatePostRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 좋아요 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostLikeAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 블로그_이름;
    private UpdatePostRequest 공개_포스트를_보호로_바꾸는_요청;
    private UpdatePostRequest 공개_포스트를_비공개로_바꾸는_요청;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        블로그_이름 = 블로그_개설(말랑_세션_ID, "mallang-log");
        공개_포스트를_보호로_바꾸는_요청 = new UpdatePostRequest(
                "보호로 변경",
                "보호",
                null,
                "인트로",
                PROTECTED,
                "1234",
                null,
                emptyList()
        );
        공개_포스트를_비공개로_바꾸는_요청 = new UpdatePostRequest(
                "보호로 변경",
                "보호",
                null,
                "인트로",
                PRIVATE,
                null,
                null,
                emptyList()
        );
    }

    @Nested
    class 좋아요_API {

        @Test
        void 로그인하지_않았다면_좋아요를_누를_수_없다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));

            // when
            var 응답 = 포스트_좋아요_요청(없음(), 포스트_ID, null);

            // then
            응답_상태를_검증한다(응답, 인증되지_않음);
        }

        @Test
        void 포스트에_좋아요를_누른다() {
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));
            var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");

            // when
            var 응답 = 포스트_좋아요_요청(동훈_세션_ID, 포스트_ID, null);

            // then
            응답_상태를_검증한다(응답, 생성됨);
        }

        @Test
        void 이미_좋아요를_누른_포스트에는_중복해서_좋아요를_누를_수_없다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));
            포스트_좋아요_요청(말랑_세션_ID, 포스트_ID, null);

            // when
            var 응답 = 포스트_좋아요_요청(말랑_세션_ID, 포스트_ID, null);

            // then
            응답_상태를_검증한다(응답, 중복됨);
        }

        @Nested
        class 보호된_포스트인_경우 {

            @Nested
            class 블로그_주인인_경우 {

                @Test
                void 좋아요를_누를_수_있다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(블로그_이름));

                    // when
                    var 응답 = 포스트_좋아요_요청(말랑_세션_ID, 포스트_ID, null);

                    // then
                    응답_상태를_검증한다(응답, 생성됨);
                }
            }

            @Nested
            class 블로그_주인이_아닌_경우 {

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_일치하지_않으면_좋아요를_누를_수_없다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(블로그_이름));
                    var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");

                    // when
                    var 응답 = 포스트_좋아요_요청(동훈_세션_ID, 포스트_ID, null);

                    // then
                    응답_상태를_검증한다(응답, 권한_없음);
                }

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_일치하면_좋아요를_누를_수_있다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(블로그_이름));
                    var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");

                    // when
                    var 응답 = 포스트_좋아요_요청(동훈_세션_ID, 포스트_ID, "1234");

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
                var 응답 = 포스트_좋아요_요청(말랑_세션_ID, 포스트_ID, null);

                // then
                응답_상태를_검증한다(응답, 생성됨);
            }

            @Test
            void 블로그_주인이_아닌_경우_누를_수_없다() {
                // given
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 비공개_포스트_생성_데이터(블로그_이름));
                var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");

                // when
                var 응답 = 포스트_좋아요_요청(동훈_세션_ID, 포스트_ID, null);

                // then
                응답_상태를_검증한다(응답, 권한_없음);
            }
        }
    }

    @Nested
    class 좋아요_취소_API {

        @Test
        void 좋아요를_취소한다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));
            포스트_좋아요_요청(말랑_세션_ID, 포스트_ID, null);

            // when
            var 응답 = 좋아요_취소_요청(말랑_세션_ID, 포스트_ID, null);

            // then
            응답_상태를_검증한다(응답, 본문_없음);
        }

        @Test
        void 좋아요를_누르지_않은_경우_취소하면_예외가_발생한다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));

            // when
            var 응답 = 좋아요_취소_요청(말랑_세션_ID, 포스트_ID, null);

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
        }

        @Nested
        class 보호된_포스트인_경우 {

            @Nested
            class 블로그_주인인_경우 {

                @Test
                void 좋아요를_취소할_수_있다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(블로그_이름));
                    포스트_좋아요_요청(말랑_세션_ID, 포스트_ID, null);

                    // when
                    var 응답 = 좋아요_취소_요청(말랑_세션_ID, 포스트_ID, null);

                    // then
                    응답_상태를_검증한다(응답, 본문_없음);
                }
            }

            @Nested
            class 블로그_주인이_아닌_경우 {

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_다르면_취소할_수_없다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));
                    var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
                    포스트_좋아요_요청(동훈_세션_ID, 포스트_ID, null);
                    포스트_수정_요청(말랑_세션_ID, 포스트_ID, 공개_포스트를_비공개로_바꾸는_요청);

                    // when
                    var 응답 = 좋아요_취소_요청(동훈_세션_ID, 포스트_ID, null);

                    // then
                    응답_상태를_검증한다(응답, 권한_없음);
                }

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_일치하면_취소할_수_있다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));
                    var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
                    포스트_좋아요_요청(동훈_세션_ID, 포스트_ID, null);
                    포스트_수정_요청(말랑_세션_ID, 포스트_ID, 공개_포스트를_보호로_바꾸는_요청);

                    // when
                    var 응답 = 좋아요_취소_요청(동훈_세션_ID, 포스트_ID, "1234");

                    // then
                    응답_상태를_검증한다(응답, 본문_없음);
                }
            }
        }

        @Nested
        class 비공개_포스트인_경우 {

            @Test
            void 블로그_주인은_취소할_수_있다() {
                // given
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 비공개_포스트_생성_데이터(블로그_이름));
                포스트_좋아요_요청(말랑_세션_ID, 포스트_ID, null);

                // when
                var 응답 = 좋아요_취소_요청(말랑_세션_ID, 포스트_ID, null);

                // then
                응답_상태를_검증한다(응답, 본문_없음);
            }

            @Test
            void 블로그_주인이_아닌_경우_취소할_수_없다() {
                // given
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(블로그_이름));
                var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
                포스트_좋아요_요청(동훈_세션_ID, 포스트_ID, null);
                포스트_수정_요청(말랑_세션_ID, 포스트_ID, 공개_포스트를_비공개로_바꾸는_요청);

                // when
                var 응답 = 좋아요_취소_요청(동훈_세션_ID, 포스트_ID, null);

                // then
                응답_상태를_검증한다(응답, 권한_없음);
            }
        }
    }
}
