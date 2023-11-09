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
import static com.mallang.acceptance.blog.BlogAcceptanceTestHelper.블로그_개설;
import static com.mallang.acceptance.post.PostAcceptanceTestHelper.포스트_생성;
import static com.mallang.acceptance.post.PostLikeAcceptanceSteps.좋아요_취소_요청;
import static com.mallang.acceptance.post.PostLikeAcceptanceSteps.포스트_좋아요_요청;
import static com.mallang.post.domain.visibility.PostVisibility.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibility.Visibility.PROTECTED;

import com.mallang.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 좋아요 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class PostLikeAcceptanceTest extends AcceptanceTest {

    @Test
    void 로그인하지_않았다면_좋아요를_누를_수_없다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        var 포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "첫 포스트", "첫 포스트이네요.", 없음(), "태그1", "태그2");

        // when
        var 응답 = 포스트_좋아요_요청(없음(), 포스트_ID);

        // then
        응답_상태를_검증한다(응답, 인증되지_않음);
    }

    @Test
    void 포스트에_좋아요를_누른다() {
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        var 포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "첫 포스트", "첫 포스트이네요.", 없음(), "태그1", "태그2");

        // when
        var 응답 = 포스트_좋아요_요청(말랑_세션_ID, 포스트_ID);

        // then
        응답_상태를_검증한다(응답, 생성됨);
    }

    @Test
    void 이미_좋아요를_누른_포스트에는_중복해서_좋아요를_누를_수_없다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        var 포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "첫 포스트", "첫 포스트이네요.", 없음(), "태그1", "태그2");
        포스트_좋아요_요청(말랑_세션_ID, 포스트_ID);

        // when
        var 응답 = 포스트_좋아요_요청(말랑_세션_ID, 포스트_ID);

        // then
        응답_상태를_검증한다(응답, 중복됨);
    }

    @Test
    void 좋아요를_취소한다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        var 포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "첫 포스트", "첫 포스트이네요.", 없음(), "태그1", "태그2");
        포스트_좋아요_요청(말랑_세션_ID, 포스트_ID);

        // when
        var 응답 = 좋아요_취소_요청(말랑_세션_ID, 포스트_ID);

        // then
        응답_상태를_검증한다(응답, 본문_없음);
    }

    @Test
    void 좋아요를_누르지_않은_경우_취소하면_예외가_발생한다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        var 포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "첫 포스트", "첫 포스트이네요.", 없음(), "태그1", "태그2");

        // when
        var 응답 = 좋아요_취소_요청(말랑_세션_ID, 포스트_ID);

        // then
        응답_상태를_검증한다(응답, 찾을수_없음);
    }

    @Test
    void 블로그_주인이_아닌_경우_보호되었거나_비공개된_글에는_해당_API로_좋아요를_누르거나_취소할_수_없다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        var 비공개_포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID,
                "첫 포스트", "첫 포스트이네요.",
                PRIVATE, 없음(),
                없음(), "태그1", "태그2");
        var 보호_포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID,
                "첫 포스트", "첫 포스트이네요.",
                PROTECTED, "1234",
                없음(), "태그1", "태그2");

        // when
        var 비공개_응답 = 포스트_좋아요_요청(동훈_세션_ID, 비공개_포스트_ID);
        var 보호_응답 = 포스트_좋아요_요청(동훈_세션_ID, 보호_포스트_ID);

        // then
        응답_상태를_검증한다(비공개_응답, 권한_없음);
        응답_상태를_검증한다(보호_응답, 권한_없음);
    }

    @Test
    void 블로그_주인은_경우_보호되었거나_비공개된_글에도_좋아요를_누르거나_취소할_수_있다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        var 비공개_포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID,
                "첫 포스트", "첫 포스트이네요.",
                PRIVATE, 없음(),
                없음(), "태그1", "태그2");
        var 보호_포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID,
                "첫 포스트", "첫 포스트이네요.",
                PROTECTED, "1234",
                없음(), "태그1", "태그2");

        // when
        var 비공개_응답 = 포스트_좋아요_요청(말랑_세션_ID, 비공개_포스트_ID);
        var 보호_응답 = 포스트_좋아요_요청(말랑_세션_ID, 보호_포스트_ID);

        // then
        응답_상태를_검증한다(비공개_응답, 생성됨);
        응답_상태를_검증한다(보호_응답, 생성됨);
    }
}
