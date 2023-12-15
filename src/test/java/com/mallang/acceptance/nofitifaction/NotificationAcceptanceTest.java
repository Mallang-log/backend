package com.mallang.acceptance.nofitifaction;

import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.blog.subsribe.BlogSubscribeAcceptanceSteps.블로그_구독_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.댓글_작성;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.비인증_댓글_작성;
import static com.mallang.acceptance.nofitifaction.NotificationAcceptanceSteps.내_알림_목록_조회_요청;
import static com.mallang.acceptance.nofitifaction.NotificationAcceptanceSteps.알림_읽을_처리_요청;
import static com.mallang.acceptance.nofitifaction.NotificationAcceptanceSteps.알림_제거_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.공개_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.comment.presentation.request.WriteAuthCommentRequest;
import com.mallang.comment.presentation.request.WriteUnAuthCommentRequest;
import com.mallang.notification.query.response.NotificationListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("알림 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class NotificationAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션;
    private String 동훈_세션;
    private String 말랑_블로그_이름;
    private Long 말랑_공개_포스트_ID;
    private Long 동훈_댓글_ID;
    private Long 비인증_대댓글_ID;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션 = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션 = 회원가입과_로그인_후_세션_ID_반환("동훈");
        말랑_블로그_이름 = 블로그_개설(말랑_세션, "mallang-log");
        말랑_공개_포스트_ID = 포스트_생성(말랑_세션, 공개_포스트_생성_데이터(말랑_블로그_이름));

        var 동훈_댓글_요청 = new WriteAuthCommentRequest(말랑_공개_포스트_ID, 말랑_블로그_이름, "댓글", true, null);
        동훈_댓글_ID = 댓글_작성(동훈_세션, 동훈_댓글_요청, null);

        var 익명_대댓글_요청 = new WriteUnAuthCommentRequest(말랑_공개_포스트_ID, 말랑_블로그_이름, "댓글", "Hi", "1234", 동훈_댓글_ID);
        비인증_대댓글_ID = 비인증_댓글_작성(익명_대댓글_요청, null);

        블로그_구독_요청(동훈_세션, 말랑_블로그_이름);
    }


    @Nested
    class 내_알림_목록_조회_API {

        @Test
        void 내_알림_목록을_조회한다() {
            // when
            var 응답 = 내_알림_목록_조회_요청(말랑_세션);

            // then
            assertThat(응답.currentPage()).isZero();
            assertThat(응답.currentElementsCount()).isEqualTo(3);
        }
    }

    @Nested
    class 알림_읽을_처리_API {

        @Test
        void 내_알림이_아니라면_예외() {
            // given
            var 알림_목록 = 내_알림_목록_조회_요청(말랑_세션);
            NotificationListResponse firstNotification = 알림_목록.content().get(0);

            // when
            var 응답 = 알림_읽을_처리_요청(동훈_세션, firstNotification.id());

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 알림을_읽음_처리한다() {
            // given
            var 알림_목록 = 내_알림_목록_조회_요청(말랑_세션);
            NotificationListResponse firstNotification = 알림_목록.content().get(0);

            // when
            var 응답 = 알림_읽을_처리_요청(말랑_세션, firstNotification.id());

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }
    }

    @Nested
    class 알림_제거_API {

        @Test
        void 내_알림이_아니라면_예외() {
            // given
            var 알림_목록 = 내_알림_목록_조회_요청(말랑_세션);
            NotificationListResponse firstNotification = 알림_목록.content().get(0);

            // when
            var 응답 = 알림_제거_요청(동훈_세션, firstNotification.id());

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 알림을_제거한다() {
            // given
            var 알림_목록 = 내_알림_목록_조회_요청(말랑_세션);
            NotificationListResponse firstNotification = 알림_목록.content().get(0);

            // when
            var 응답 = 알림_제거_요청(말랑_세션, firstNotification.id());

            // then
            응답_상태를_검증한다(응답, 본문_없음);
        }
    }
}
