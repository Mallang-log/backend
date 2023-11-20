package com.mallang.acceptance.comment;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.비어있음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.잘못된_요청;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.공개;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.댓글_작성;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.댓글_작성_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.비공개;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.비인증_댓글_삭제_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.비인증_댓글_수정_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.비인증_댓글_작성;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.비인증_댓글_작성_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.비인증_댓글_작성자_데이터;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.비인증_댓글_조회_데이터;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.삭제되지_않음;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.삭제됨;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.인증된_댓글_삭제_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.인증된_댓글_수정_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.인증된_댓글_작성자_데이터;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.인증된_댓글_조회_데이터;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.특정_포스트의_댓글_전체_조회_응답을_검증한다;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.특정_포스팅의_댓글_전체_조회;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.공개_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.보호_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.비공개_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_수정_요청;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;

import com.mallang.acceptance.AcceptanceTest;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private String 말랑_블로그_이름;

    @SneakyThrows
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        말랑_블로그_이름 = 블로그_개설(말랑_세션_ID, "mallang-log");
    }

    @Nested
    class 포스트에_댓글_작성_시 extends AcceptanceTest {

        @Test
        void 비인증으로_댓글을_작성한다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));

            // when
            var 응답 = 비인증_댓글_작성_요청(포스트_ID, "댓글", "비인증", "1234", null);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            var 댓글_ID = ID를_추출한다(응답);
            값이_존재한다(댓글_ID);
        }

        @Test
        void 로그인한_사용자가_댓글을_작성한다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));

            // when
            var 응답 = 댓글_작성_요청(동훈_세션_ID, 포스트_ID, "댓글", 공개, null);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            var 댓글_Id = ID를_추출한다(응답);
            값이_존재한다(댓글_Id);
        }

        @Test
        void 로그인한_사용자는_비밀_댓글을_작성할_수_있다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));

            // when
            var 응답 = 댓글_작성_요청(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            var 댓글_ID = ID를_추출한다(응답);
            값이_존재한다(댓글_ID);
        }

        @Test
        void 대댓글을_작성할_수_있다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);

            // when
            var 응답 = 댓글_작성_요청(동훈_세션_ID, 포스트_ID, "댓글", 비공개, 댓글_ID, null);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            var 대댓글_ID = ID를_추출한다(응답);
            값이_존재한다(대댓글_ID);
        }

        @Test
        void 대댓글에_대해_댓글을_달_수_없다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);
            var 대댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "댓글", 비공개, 댓글_ID, null);

            // when
            var 응답 = 댓글_작성_요청(동훈_세션_ID, 포스트_ID, "댓글", 비공개, 대댓글_ID, null);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
        }

        @Nested
        class 보호된_포스트인_경우 extends AcceptanceTest {

            @Nested
            class 블로그_주인인_경우 extends AcceptanceTest {

                @Test
                void 작성_가능하다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(말랑_블로그_이름));

                    // when
                    var 응답 = 댓글_작성_요청(말랑_세션_ID, 포스트_ID, "댓글", 비공개, null);

                    // then
                    응답_상태를_검증한다(응답, 생성됨);
                }
            }

            @Nested
            class 블로그_주인이_아닌_경우 extends AcceptanceTest {

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_다르다면_쓸_수_없다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(말랑_블로그_이름));

                    // when
                    var 응답 = 댓글_작성_요청(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);

                    // then
                    응답_상태를_검증한다(응답, 권한_없음);
                }

                @Test
                void 대댓글로_남기는_것_역시_불가하다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                    var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);

                    포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                            "보호로 변경", "보호", 없음(), "인트로",
                            PROTECTED, "1234", 없음());

                    // when
                    var 응답 = 댓글_작성_요청(동훈_세션_ID, 포스트_ID, "댓글", 비공개, 댓글_ID, null);

                    // then
                    응답_상태를_검증한다(응답, 권한_없음);
                }

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_일치하면_작성할_수_있다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(말랑_블로그_이름));

                    // when
                    var 응답 = 댓글_작성_요청(동훈_세션_ID, 포스트_ID, "댓글", 비공개, "1234");

                    // then
                    응답_상태를_검증한다(응답, 생성됨);
                }

                @Test
                void 회원이_아니어도_입력한_비밀번호가_포스트의_비밀번호와_일치하면_작성할_수_있다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(말랑_블로그_이름));

                    // when
                    var 응답 = 비인증_댓글_작성_요청(null, 포스트_ID, "댓글", "익명입니다", "댓글비번", "1234");

                    // then
                    응답_상태를_검증한다(응답, 생성됨);
                }
            }
        }

        @Nested
        class 비공개_포스트인_경우 extends AcceptanceTest {

            @Test
            void 블로그_주인은_작성_가능하다() {
                // given
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 비공개_포스트_생성_데이터(말랑_블로그_이름));

                // when
                var 응답 = 댓글_작성_요청(말랑_세션_ID, 포스트_ID, "댓글", 비공개, null);

                // then
                응답_상태를_검증한다(응답, 생성됨);
            }

            @Test
            void 블로그_주인이_아닌_경우_작성할_수_없다() {
                // given
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 비공개_포스트_생성_데이터(말랑_블로그_이름));

                // when
                var 응답 = 댓글_작성_요청(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);

                // then
                응답_상태를_검증한다(응답, 권한_없음);
            }
        }
    }

    @Nested
    class 댓글_수정_시 extends AcceptanceTest {

        @Test
        void 자신의_댓글을_수정한다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "좋은 글 감사합니다", 비공개, null);

            // when
            var 응답 = 인증된_댓글_수정_요청(동훈_세션_ID, 댓글_ID, "수정", 공개, null);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
            var 예상_데이터 = List.of(
                    인증된_댓글_조회_데이터(댓글_ID, "수정", 공개,
                            인증된_댓글_작성자_데이터("동훈", "동훈"),
                            삭제되지_않음)
            );
            var 댓글_조회_응답 = 특정_포스팅의_댓글_전체_조회(포스트_ID, null);
            특정_포스트의_댓글_전체_조회_응답을_검증한다(댓글_조회_응답, 예상_데이터);
        }

        @Test
        void 자신의_댓글이_아닌_경우_예외() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "좋은 글 감사합니다", 비공개, null);

            // when
            var 응답 = 인증된_댓글_수정_요청(말랑_세션_ID, 댓글_ID, "수정", 공개, null);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 비인증_댓글을_수정한다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 비인증_댓글_작성(포스트_ID, "좋은 글 감사합니다", "비인증입니다", "1234", null);

            // when
            var 응답 = 비인증_댓글_수정_요청(댓글_ID, "1234", "수정", null);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 비인증_댓글_수정_시_비밀번호가_다르면_예외() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 비인증_댓글_작성(포스트_ID, "좋은 글 감사합니다", "비인증입니다", "1234", null);

            // when
            var 응답 = 비인증_댓글_수정_요청(댓글_ID, "12345", "수정", null);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 포스트_작성자라도_타인의_댓글을_수정할_수는_없다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "좋은 글 감사합니다", 비공개, null);

            // when
            var 응답 = 인증된_댓글_수정_요청(말랑_세션_ID, 댓글_ID, "수정", 비공개, null);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Nested
        class 보호된_포스트인_경우 extends AcceptanceTest {

            @Nested
            class 블로그_주인인_경우 extends AcceptanceTest {

                @Test
                void 수정_가능하다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(말랑_블로그_이름));
                    var 댓글_ID = 댓글_작성(말랑_세션_ID, 포스트_ID, "댓글", 비공개, null);

                    // when
                    var 응답 = 인증된_댓글_수정_요청(말랑_세션_ID, 댓글_ID, "수정", 비공개, null);

                    // then
                    응답_상태를_검증한다(응답, 정상_처리);
                }
            }

            @Nested
            class 블로그_주인이_아닌_경우 extends AcceptanceTest {

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_다르다면_수정할_수_없다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                    var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);
                    포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                            "보호로 변경", "보호", 없음(), "인트로",
                            PROTECTED, "1234", 없음());

                    // when
                    var 응답 = 인증된_댓글_수정_요청(동훈_세션_ID, 댓글_ID, "수정", 비공개, null);

                    // then
                    응답_상태를_검증한다(응답, 권한_없음);
                }

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_일치하면_수정할_수_있다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                    var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);
                    포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                            "보호로 변경", "보호", "인트로", 없음(),
                            PROTECTED, "1234", 없음());

                    // when
                    var 응답 = 인증된_댓글_수정_요청(동훈_세션_ID, 댓글_ID, "수정", 비공개, "1234");

                    // then
                    응답_상태를_검증한다(응답, 정상_처리);
                }
            }
        }

        @Nested
        class 비공개_포스트인_경우 extends AcceptanceTest {

            @Test
            void 블로그_주인은_수정_가능하다() {
                // given
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 비공개_포스트_생성_데이터(말랑_블로그_이름));
                var 댓글_ID = 댓글_작성(말랑_세션_ID, 포스트_ID, "댓글", 비공개, null);

                // when
                var 응답 = 인증된_댓글_수정_요청(말랑_세션_ID, 댓글_ID, "수정", 비공개, null);

                // then
                응답_상태를_검증한다(응답, 정상_처리);
            }

            @Test
            void 블로그_주인이_아닌_경우_수정할_수_없다() {
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);
                포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                        "보호로 변경", "보호", 없음(), "인트로",
                        PRIVATE, 없음(), 없음());

                // when
                var 응답 = 인증된_댓글_수정_요청(동훈_세션_ID, 댓글_ID, "수정", 비공개, null);

                // then
                응답_상태를_검증한다(응답, 권한_없음);
            }
        }
    }

    @Nested
    class 댓글_삭제_시 extends AcceptanceTest {

        @Test
        void 자신의_댓글을_삭제한다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "좋은 글 감사합니다", 비공개, null);

            // when
            var 응답 = 인증된_댓글_삭제_요청(동훈_세션_ID, 댓글_ID, null);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
            var 댓글_조회_응답 = 특정_포스팅의_댓글_전체_조회(포스트_ID, null);
            특정_포스트의_댓글_전체_조회_응답을_검증한다(댓글_조회_응답, 비어있음());
        }

        @Test
        void 자신의_댓글이_아닌_경우_예외() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 댓글_작성(말랑_세션_ID, 포스트_ID, "좋은 글 감사합니다", 비공개, null);

            // when
            var 응답 = 인증된_댓글_삭제_요청(동훈_세션_ID, 댓글_ID, null);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 비인증_댓글을_삭제한다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 비인증_댓글_작성(포스트_ID, "좋은 글 감사합니다", "비인증입니다", "1234", null);

            // when
            var 응답 = 비인증_댓글_삭제_요청(댓글_ID, "1234", null);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 비인증_댓글_삭제_시_비밀번호가_다르면_예외() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 비인증_댓글_작성(포스트_ID, "좋은 글 감사합니다", "비인증입니다", "1234", null);

            // when
            var 응답 = 비인증_댓글_삭제_요청(댓글_ID, "123", null);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 포스트_작성자는_댓글을_삭제가_가능하다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 비인증_댓글_작성(포스트_ID, "좋은 글 감사합니다", "비인증입니다", "1234", null);

            // when
            var 응답 = 비인증_댓글_삭제_요청(말랑_세션_ID, 댓글_ID, null);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
            var 댓글_조회_응답 = 특정_포스팅의_댓글_전체_조회(포스트_ID, null);
            특정_포스트의_댓글_전체_조회_응답을_검증한다(댓글_조회_응답, 비어있음());
        }

        @Test
        void 대댓글_제거_시_부모_댓글과의_관계도_끊어진다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 비인증_댓글_작성(포스트_ID, "좋은 글 감사합니다", "비인증입니다", "1234", null);
            var 대댓글_ID = 댓글_작성(말랑_세션_ID, 포스트_ID, "대댓글입니다", 공개, 댓글_ID, null);

            // when
            var 응답 = 인증된_댓글_삭제_요청(말랑_세션_ID, 대댓글_ID, null);

            // then
            var 댓글_조회_응답 = 특정_포스팅의_댓글_전체_조회(포스트_ID, null);
            응답_상태를_검증한다(응답, 정상_처리);
            특정_포스트의_댓글_전체_조회_응답을_검증한다(댓글_조회_응답,
                    List.of(
                            비인증_댓글_조회_데이터(
                                    댓글_ID,
                                    "좋은 글 감사합니다",
                                    비인증_댓글_작성자_데이터("비인증입니다"),
                                    삭제되지_않음
                            )
                    ));
        }

        @Test
        void 댓글_제거_시_자식_댓글이_존재한다면_부모와의_연관관계는_유지되며_논리적으로만_제거시킨다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 비인증_댓글_작성(포스트_ID, "좋은 글 감사합니다", "비인증 말랑", "1234", null);
            var 대댓글_ID = 댓글_작성(말랑_세션_ID, 포스트_ID, "대댓글입니다", 공개, 댓글_ID, null);
            비인증_댓글_삭제_요청(댓글_ID, "1234", null);

            // when
            var 응답 = 비인증_댓글_삭제_요청(댓글_ID, "1234", null);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
            var 댓글_조회_응답 = 특정_포스팅의_댓글_전체_조회(포스트_ID, null);
            특정_포스트의_댓글_전체_조회_응답을_검증한다(댓글_조회_응답,
                    List.of(
                            비인증_댓글_조회_데이터(
                                    댓글_ID,
                                    "삭제된 댓글입니다.",
                                    비인증_댓글_작성자_데이터("비인증 말랑"),
                                    삭제됨,
                                    인증된_댓글_조회_데이터(대댓글_ID,
                                            "대댓글입니다",
                                            공개,
                                            인증된_댓글_작성자_데이터("말랑", "말랑"),
                                            삭제되지_않음
                                    )
                            )
                    ));
        }

        @Test
        void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태이며_더이상_존재하는_자식이_없는_경우_부모_댓글도_물리적으로_제거된다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 비인증_댓글_작성(포스트_ID, "좋은 글 감사합니다", "비인증입니다", "1234", null);
            var 대댓글_ID = 댓글_작성(말랑_세션_ID, 포스트_ID, "대댓글입니다", 공개, 댓글_ID, null);
            비인증_댓글_삭제_요청(댓글_ID, "1234", null);

            // when
            var 응답 = 인증된_댓글_삭제_요청(말랑_세션_ID, 대댓글_ID, null);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
            var 댓글_조회_응답 = 특정_포스팅의_댓글_전체_조회(포스트_ID, null);
            특정_포스트의_댓글_전체_조회_응답을_검증한다(댓글_조회_응답, 비어있음());
        }

        @Nested
        class 보호된_포스트인_경우 extends AcceptanceTest {

            @Nested
            class 블로그_주인인_경우 extends AcceptanceTest {

                @Test
                void 삭제_가능하다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_생성_데이터(말랑_블로그_이름));
                    var 댓글_ID = 댓글_작성(말랑_세션_ID, 포스트_ID, "댓글", 비공개, null);

                    // when
                    var 응답 = 인증된_댓글_수정_요청(말랑_세션_ID, 댓글_ID, "수정", 비공개, null);

                    // then
                    응답_상태를_검증한다(응답, 정상_처리);
                }
            }

            @Nested
            class 블로그_주인이_아닌_경우 extends AcceptanceTest {

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_다르다면_삭제할_수_없다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                    var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);
                    포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                            "보호로 변경", "보호", 없음(), "인트로",
                            PROTECTED, "1234", 없음());

                    // when
                    var 응답 = 인증된_댓글_삭제_요청(동훈_세션_ID, 댓글_ID, null);

                    // then
                    응답_상태를_검증한다(응답, 권한_없음);
                }

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_일치하면_삭제할_수_있다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                    var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);
                    포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                            "보호로 변경", "보호", "인트로", 없음(),
                            PROTECTED, "1234", 없음());

                    // when
                    var 응답 = 인증된_댓글_삭제_요청(동훈_세션_ID, 댓글_ID, "1234");

                    // then
                    응답_상태를_검증한다(응답, 정상_처리);
                }
            }
        }

        @Nested
        class 비공개_포스트인_경우 extends AcceptanceTest {

            @Test
            void 블로그_주인은_삭제_가능하다() {
                // given
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 비공개_포스트_생성_데이터(말랑_블로그_이름));
                var 댓글_ID = 댓글_작성(말랑_세션_ID, 포스트_ID, "댓글", 비공개, null);

                // when
                var 응답 = 인증된_댓글_삭제_요청(말랑_세션_ID, 댓글_ID, null);

                // then
                응답_상태를_검증한다(응답, 정상_처리);
            }

            @Test
            void 블로그_주인이_아닌_경우_삭제할_수_없다() {
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "댓글", 비공개, null);
                포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                        "보호로 변경", "보호", 없음(), "인트로",
                        PRIVATE, 없음(), 없음());

                // when
                var 응답 = 인증된_댓글_삭제_요청(동훈_세션_ID, 댓글_ID, null);

                // then
                응답_상태를_검증한다(응답, 권한_없음);
            }
        }
    }

    @Nested
    class 특정_포스트의_댓글_전체_조회_시 extends AcceptanceTest {

        private String 후후_세션_ID;

        @BeforeEach
        protected void setUp() {
            후후_세션_ID = 회원가입과_로그인_후_세션_ID_반환("후후");
        }

        @Test
        void 로그인하지_않은_경우_비밀_댓글은_비밀_댓글입니다로_처리되어_조회된다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 헤헤_댓글_ID = 비인증_댓글_작성(포스트_ID, "헤헤 댓글", "헤헤", "1234", null);

            var 동훈_공개_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "동훈 댓글", 공개, null);
            var 동훈_비밀_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "[비밀] 동훈 댓글", 비공개, null);

            // when
            var 응답 = 특정_포스팅의_댓글_전체_조회(포스트_ID, null);

            // then
            var 예상_데이터 = List.of(
                    비인증_댓글_조회_데이터(헤헤_댓글_ID, "헤헤 댓글", 비인증_댓글_작성자_데이터("헤헤"), 삭제되지_않음),
                    인증된_댓글_조회_데이터(동훈_공개_댓글_ID, "동훈 댓글", 공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음),
                    인증된_댓글_조회_데이터(동훈_비밀_댓글_ID, "비밀 댓글입니다.", 비공개, 인증된_댓글_작성자_데이터("익명", null), 삭제되지_않음)
            );
            특정_포스트의_댓글_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 로그인한_경우_내가_쓴_비밀_댓글을_포함한_댓글들이_전체_조회된다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));

            var 헤헤_댓글_ID = 비인증_댓글_작성(포스트_ID, "헤헤 댓글", "헤헤", "1234", null);

            var 동훈_공개_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "동훈 댓글", 공개, null);
            var 동훈_비밀_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "[비밀] 동훈 댓글", 비공개, null);

            var 후후_공개_댓글_ID = 댓글_작성(후후_세션_ID, 포스트_ID, "후후 댓글", 공개, null);
            var 후후_비밀_댓글_ID = 댓글_작성(후후_세션_ID, 포스트_ID, "[비밀] 후후 댓글", 비공개, null);

            // when
            var 응답 = 특정_포스팅의_댓글_전체_조회(동훈_세션_ID, 포스트_ID, null);

            // then
            var 예상_데이터 = List.of(
                    비인증_댓글_조회_데이터(헤헤_댓글_ID, "헤헤 댓글", 비인증_댓글_작성자_데이터("헤헤"), 삭제되지_않음),
                    인증된_댓글_조회_데이터(동훈_공개_댓글_ID, "동훈 댓글", 공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음),
                    인증된_댓글_조회_데이터(동훈_비밀_댓글_ID, "[비밀] 동훈 댓글", 비공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음),
                    인증된_댓글_조회_데이터(후후_공개_댓글_ID, "후후 댓글", 공개, 인증된_댓글_작성자_데이터("후후", "후후"), 삭제되지_않음),
                    인증된_댓글_조회_데이터(후후_비밀_댓글_ID, "비밀 댓글입니다.", 비공개, 인증된_댓글_작성자_데이터("익명", null), 삭제되지_않음)
            );
            특정_포스트의_댓글_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 내가_쓴_댓글에_대한_다른_사람의_비밀_대댓글은_댓글_작성자가_볼_수_있다() {
            // given
            var 헤나_세션_ID = 회원가입과_로그인_후_세션_ID_반환("헤나");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));

            var 동훈_비공개_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "[비밀] 동훈 댓글", 비공개, null);

            var 헤나_비밀_댓글_ID = 댓글_작성(헤나_세션_ID, 포스트_ID, "[비밀] 헤나 댓글", 비공개, 동훈_비공개_댓글_ID, null);
            var 후후_비밀_댓글_ID = 댓글_작성(후후_세션_ID, 포스트_ID, "[비밀] 후후 댓글", 비공개, 동훈_비공개_댓글_ID, null);

            // when
            var 댓글_작성자_조회_결과 = 특정_포스팅의_댓글_전체_조회(동훈_세션_ID, 포스트_ID, null);
            var 대댓글_작성자_조회_결과1 = 특정_포스팅의_댓글_전체_조회(헤나_세션_ID, 포스트_ID, null);
            var 대댓글_작성자_조회_결과2 = 특정_포스팅의_댓글_전체_조회(후후_세션_ID, 포스트_ID, null);

            // then
            var 댓글_작성자_예상_데이터 = List.of(
                    인증된_댓글_조회_데이터(동훈_비공개_댓글_ID, "[비밀] 동훈 댓글", 비공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음,
                            인증된_댓글_조회_데이터(헤나_비밀_댓글_ID, "[비밀] 헤나 댓글", 비공개, 인증된_댓글_작성자_데이터("헤나", "헤나"), 삭제되지_않음),
                            인증된_댓글_조회_데이터(후후_비밀_댓글_ID, "[비밀] 후후 댓글", 비공개, 인증된_댓글_작성자_데이터("후후", "후후"), 삭제되지_않음)
                    ));
            var 대댓글_작성자_예상_데이터1 = List.of(
                    인증된_댓글_조회_데이터(동훈_비공개_댓글_ID, "비밀 댓글입니다.", 비공개, 인증된_댓글_작성자_데이터("익명", null), 삭제되지_않음,
                            인증된_댓글_조회_데이터(헤나_비밀_댓글_ID, "[비밀] 헤나 댓글", 비공개, 인증된_댓글_작성자_데이터("헤나", "헤나"), 삭제되지_않음),
                            인증된_댓글_조회_데이터(후후_비밀_댓글_ID, "비밀 댓글입니다.", 비공개, 인증된_댓글_작성자_데이터("익명", null), 삭제되지_않음)
                    ));
            var 대댓글_작성자_예상_데이터2 = List.of(
                    인증된_댓글_조회_데이터(동훈_비공개_댓글_ID, "비밀 댓글입니다.", 비공개, 인증된_댓글_작성자_데이터("익명", null), 삭제되지_않음,
                            인증된_댓글_조회_데이터(헤나_비밀_댓글_ID, "비밀 댓글입니다.", 비공개, 인증된_댓글_작성자_데이터("익명", null), 삭제되지_않음),
                            인증된_댓글_조회_데이터(후후_비밀_댓글_ID, "[비밀] 후후 댓글", 비공개, 인증된_댓글_작성자_데이터("후후", "후후"), 삭제되지_않음)
                    ));
            특정_포스트의_댓글_전체_조회_응답을_검증한다(댓글_작성자_조회_결과, 댓글_작성자_예상_데이터);
            특정_포스트의_댓글_전체_조회_응답을_검증한다(대댓글_작성자_조회_결과1, 대댓글_작성자_예상_데이터1);
            특정_포스트의_댓글_전체_조회_응답을_검증한다(대댓글_작성자_조회_결과2, 대댓글_작성자_예상_데이터2);
        }

        @Test
        void 포스트_작성자인_경우_모든_비밀_댓글을_포함한_전체_댓글이_조회된다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));

            var 헤헤_댓글_ID = 비인증_댓글_작성(포스트_ID, "헤헤 댓글", "헤헤", "1234", null);

            var 동훈_공개_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "동훈 댓글", 공개, null);
            var 동훈_비밀_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "[비밀] 동훈 댓글", 비공개, null);

            var 후후_공개_댓글_ID = 댓글_작성(후후_세션_ID, 포스트_ID, "후후 댓글", 공개, null);
            var 후후_비밀_댓글_ID = 댓글_작성(후후_세션_ID, 포스트_ID, "[비밀] 후후 댓글", 비공개, null);

            // when
            var 응답 = 특정_포스팅의_댓글_전체_조회(말랑_세션_ID, 포스트_ID, null);

            // then
            var 예상_데이터 = List.of(
                    비인증_댓글_조회_데이터(헤헤_댓글_ID, "헤헤 댓글", 비인증_댓글_작성자_데이터("헤헤"), 삭제되지_않음),
                    인증된_댓글_조회_데이터(동훈_공개_댓글_ID, "동훈 댓글", 공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음),
                    인증된_댓글_조회_데이터(동훈_비밀_댓글_ID, "[비밀] 동훈 댓글", 비공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음),
                    인증된_댓글_조회_데이터(후후_공개_댓글_ID, "후후 댓글", 공개, 인증된_댓글_작성자_데이터("후후", "후후"), 삭제되지_않음),
                    인증된_댓글_조회_데이터(후후_비밀_댓글_ID, "[비밀] 후후 댓글", 비공개, 인증된_댓글_작성자_데이터("후후", "후후"), 삭제되지_않음)
            );
            특정_포스트의_댓글_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 삭제된_댓글은_삭제된_댓글입니다로_처리되어_조회된다() {
            // given
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 댓글_ID = 비인증_댓글_작성(포스트_ID, "좋은 글 감사합니다", "비인증 말랑", "1234", null);
            var 대댓글_ID = 댓글_작성(말랑_세션_ID, 포스트_ID, "대댓글입니다", 공개, 댓글_ID, null);
            비인증_댓글_삭제_요청(댓글_ID, "1234", null);

            // when
            var 응답 = 비인증_댓글_삭제_요청(댓글_ID, "1234", null);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
            var 댓글_조회_응답 = 특정_포스팅의_댓글_전체_조회(포스트_ID, null);
            특정_포스트의_댓글_전체_조회_응답을_검증한다(댓글_조회_응답,
                    List.of(
                            비인증_댓글_조회_데이터(
                                    댓글_ID,
                                    "삭제된 댓글입니다.",
                                    비인증_댓글_작성자_데이터("비인증 말랑"),
                                    삭제됨,
                                    인증된_댓글_조회_데이터(
                                            대댓글_ID,
                                            "대댓글입니다",
                                            공개,
                                            인증된_댓글_작성자_데이터("말랑", "말랑"),
                                            삭제되지_않음
                                    )
                            )
                    ));
        }

        @Nested
        class 보호된_포스트인_경우 extends AcceptanceTest {

            @Nested
            class 블로그_주인인_경우 extends AcceptanceTest {

                @Test
                void 조회_가능하다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                    var 헤헤_댓글_ID = 비인증_댓글_작성(포스트_ID, "헤헤 댓글", "헤헤", "1234", null);
                    var 동훈_공개_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "동훈 댓글", 공개, null);
                    var 동훈_비밀_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "[비밀] 동훈 댓글", 비공개, null);
                    var 후후_공개_댓글_ID = 댓글_작성(후후_세션_ID, 포스트_ID, "후후 댓글", 공개, null);
                    var 후후_비밀_댓글_ID = 댓글_작성(후후_세션_ID, 포스트_ID, "[비밀] 후후 댓글", 비공개, null);

                    포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                            "보호로 변경", "보호", "인트로", 없음(),
                            PROTECTED, "1234", 없음());

                    // when
                    var 응답 = 특정_포스팅의_댓글_전체_조회(말랑_세션_ID, 포스트_ID, null);

                    // then
                    var 예상_데이터 = List.of(
                            비인증_댓글_조회_데이터(헤헤_댓글_ID, "헤헤 댓글", 비인증_댓글_작성자_데이터("헤헤"), 삭제되지_않음),
                            인증된_댓글_조회_데이터(동훈_공개_댓글_ID, "동훈 댓글", 공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음),
                            인증된_댓글_조회_데이터(동훈_비밀_댓글_ID, "[비밀] 동훈 댓글", 비공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음),
                            인증된_댓글_조회_데이터(후후_공개_댓글_ID, "후후 댓글", 공개, 인증된_댓글_작성자_데이터("후후", "후후"), 삭제되지_않음),
                            인증된_댓글_조회_데이터(후후_비밀_댓글_ID, "[비밀] 후후 댓글", 비공개, 인증된_댓글_작성자_데이터("후후", "후후"), 삭제되지_않음)
                    );
                    특정_포스트의_댓글_전체_조회_응답을_검증한다(응답, 예상_데이터);
                }
            }

            @Nested
            class 블로그_주인이_아닌_경우 extends AcceptanceTest {

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_다르다면_조회할_수_없다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                    비인증_댓글_작성(포스트_ID, "헤헤 댓글", "헤헤", "1234", null);
                    댓글_작성(동훈_세션_ID, 포스트_ID, "동훈 댓글", 공개, null);
                    댓글_작성(동훈_세션_ID, 포스트_ID, "[비밀] 동훈 댓글", 비공개, null);
                    댓글_작성(후후_세션_ID, 포스트_ID, "후후 댓글", 공개, null);
                    댓글_작성(후후_세션_ID, 포스트_ID, "[비밀] 후후 댓글", 비공개, null);

                    포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                            "보호로 변경", "보호", 없음(), "인트로",
                            PROTECTED, "1234", 없음());

                    // when
                    var 응답 = 특정_포스팅의_댓글_전체_조회(동훈_세션_ID, 포스트_ID, null);

                    // then
                    응답_상태를_검증한다(응답, 권한_없음);
                }

                @Test
                void 입력한_비밀번호가_포스트의_비밀번호와_일치하면_조회할_수_있다() {
                    // given
                    var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                    var 헤헤_댓글_ID = 비인증_댓글_작성(포스트_ID, "헤헤 댓글", "헤헤", "1234", null);
                    var 동훈_공개_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "동훈 댓글", 공개, null);
                    var 동훈_비밀_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "[비밀] 동훈 댓글", 비공개, null);
                    var 후후_공개_댓글_ID = 댓글_작성(후후_세션_ID, 포스트_ID, "후후 댓글", 공개, null);
                    var 후후_비밀_댓글_ID = 댓글_작성(후후_세션_ID, 포스트_ID, "[비밀] 후후 댓글", 비공개, null);

                    포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                            "보호로 변경", "보호", "인트로", 없음(),
                            PROTECTED, "1234", 없음());

                    // when
                    var 응답 = 특정_포스팅의_댓글_전체_조회(동훈_세션_ID, 포스트_ID, null);

                    // then
                    var 예상_데이터 = List.of(
                            비인증_댓글_조회_데이터(헤헤_댓글_ID, "헤헤 댓글", 비인증_댓글_작성자_데이터("헤헤"), 삭제되지_않음),
                            인증된_댓글_조회_데이터(동훈_공개_댓글_ID, "동훈 댓글", 공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음),
                            인증된_댓글_조회_데이터(동훈_비밀_댓글_ID, "[비밀] 동훈 댓글", 비공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음),
                            인증된_댓글_조회_데이터(후후_공개_댓글_ID, "후후 댓글", 공개, 인증된_댓글_작성자_데이터("후후", "후후"), 삭제되지_않음),
                            인증된_댓글_조회_데이터(후후_비밀_댓글_ID, "비밀 댓글입니다.", 비공개, 인증된_댓글_작성자_데이터("익명", null), 삭제되지_않음)
                    );
                    특정_포스트의_댓글_전체_조회_응답을_검증한다(응답, 예상_데이터);
                }
            }
        }

        @Nested
        class 비공개_포스트인_경우 extends AcceptanceTest {

            @Test
            void 블로그_주인은_조회_가능하다() {
                // given
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                var 헤헤_댓글_ID = 비인증_댓글_작성(포스트_ID, "헤헤 댓글", "헤헤", "1234", null);
                var 동훈_공개_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "동훈 댓글", 공개, null);
                var 동훈_비밀_댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "[비밀] 동훈 댓글", 비공개, null);
                var 후후_공개_댓글_ID = 댓글_작성(후후_세션_ID, 포스트_ID, "후후 댓글", 공개, null);
                var 후후_비밀_댓글_ID = 댓글_작성(후후_세션_ID, 포스트_ID, "[비밀] 후후 댓글", 비공개, null);

                포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                        "보호로 변경", "보호", "인트로", 없음(),
                        PRIVATE, 없음(), 없음());

                // when
                var 응답 = 특정_포스팅의_댓글_전체_조회(말랑_세션_ID, 포스트_ID, null);

                // then
                var 예상_데이터 = List.of(
                        비인증_댓글_조회_데이터(헤헤_댓글_ID, "헤헤 댓글", 비인증_댓글_작성자_데이터("헤헤"), 삭제되지_않음),
                        인증된_댓글_조회_데이터(동훈_공개_댓글_ID, "동훈 댓글", 공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음),
                        인증된_댓글_조회_데이터(동훈_비밀_댓글_ID, "[비밀] 동훈 댓글", 비공개, 인증된_댓글_작성자_데이터("동훈", "동훈"), 삭제되지_않음),
                        인증된_댓글_조회_데이터(후후_공개_댓글_ID, "후후 댓글", 공개, 인증된_댓글_작성자_데이터("후후", "후후"), 삭제되지_않음),
                        인증된_댓글_조회_데이터(후후_비밀_댓글_ID, "[비밀] 후후 댓글", 비공개, 인증된_댓글_작성자_데이터("후후", "후후"), 삭제되지_않음)
                );
                특정_포스트의_댓글_전체_조회_응답을_검증한다(응답, 예상_데이터);
            }

            @Test
            void 블로그_주인이_아닌_경우_조회할_수_없다() {
                var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
                비인증_댓글_작성(포스트_ID, "헤헤 댓글", "헤헤", "1234", null);
                댓글_작성(동훈_세션_ID, 포스트_ID, "동훈 댓글", 공개, null);
                댓글_작성(동훈_세션_ID, 포스트_ID, "[비밀] 동훈 댓글", 비공개, null);
                댓글_작성(후후_세션_ID, 포스트_ID, "후후 댓글", 공개, null);
                댓글_작성(후후_세션_ID, 포스트_ID, "[비밀] 후후 댓글", 비공개, null);

                포스트_수정_요청(말랑_세션_ID, 포스트_ID,
                        "보호로 변경", "보호", 없음(), "인트로",
                        PRIVATE, 없음(), 없음());

                // when
                var 응답 = 특정_포스팅의_댓글_전체_조회(동훈_세션_ID, 포스트_ID, null);

                // then
                응답_상태를_검증한다(응답, 권한_없음);
            }
        }
    }
}
