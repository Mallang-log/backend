package com.mallang.acceptance.category;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.비어있음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.잘못된_요청;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.블로그의_카테고리_조회_요청;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_생성;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_생성_요청;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_수정_요청;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_제거_요청;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_조회_응답_데이터;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_조회_응답을_검증한다;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.하위_카테고리들;
import static com.mallang.acceptance.post.PostAcceptanceSteps.보호되지_않음;
import static com.mallang.acceptance.post.PostAcceptanceSteps.좋아요_안눌림;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_내용_검증;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_데이터;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_생성;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;

import com.mallang.acceptance.AcceptanceTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("카테고리 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CategoryAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private Long 말랑_블로그_ID;
    private Long 동훈_블로그_ID;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        말랑_블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        동훈_블로그_ID = 블로그_개설(동훈_세션_ID, "donghun-log");
    }

    @Test
    void 카테고리를_생성한다() {
        // when
        var 응답 = 카테고리_생성_요청(말랑_세션_ID, 말랑_블로그_ID, "Spring", 없음());

        // then
        응답_상태를_검증한다(응답, 생성됨);
        값이_존재한다(ID를_추출한다(응답));
    }

    @Test
    void 하위_카테고리를_생성한다() {
        // given
        var 상위_카테고리_생성_응답 = 카테고리_생성_요청(말랑_세션_ID, 말랑_블로그_ID, "Spring", 없음());
        var 상위_카테고리_ID = ID를_추출한다(상위_카테고리_생성_응답);

        // when
        var 응답 = 카테고리_생성_요청(말랑_세션_ID, 말랑_블로그_ID, "JPA", 상위_카테고리_ID);

        // then
        응답_상태를_검증한다(응답, 생성됨);
        값이_존재한다(ID를_추출한다(응답));
    }

    @Test
    void 카테고리를_업데이트한다() {
        // given
        var Spring_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "Spring", 없음());
        var JPA_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "JPA", Spring_카테고리_ID);

        // when
        var extract = 카테고리_수정_요청(말랑_세션_ID, JPA_카테고리_ID, "Node", 없음());

        // then
        응답_상태를_검증한다(extract, 정상_처리);
        var 예상_응답 = List.of(
                카테고리_조회_응답_데이터(Spring_카테고리_ID, "Spring", 비어있음()),
                카테고리_조회_응답_데이터(JPA_카테고리_ID, "Node", 비어있음())
        );
        var 응답 = 블로그의_카테고리_조회_요청(말랑_블로그_ID);
        카테고리_조회_응답을_검증한다(응답, 예상_응답);
    }

    @Test
    void 특정_블로그의_카테고리를_조회한다() {
        // given
        카테고리_생성(동훈_세션_ID, 동훈_블로그_ID, "Node", 없음());

        var Spring_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "Spring", 없음());
        var JPA_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "JPA", Spring_카테고리_ID);
        var N1_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "N + 1", JPA_카테고리_ID);
        var Security_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "Security", Spring_카테고리_ID);
        var OAuth_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "OAuth", Security_카테고리_ID);
        var CSRF_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "CSRF", Security_카테고리_ID);
        var Algorithm_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "Algorithm", 없음());
        var DFS_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "DFS", Algorithm_카테고리_ID);
        var 예상_응답 = List.of(
                카테고리_조회_응답_데이터(Spring_카테고리_ID, "Spring", 하위_카테고리들(
                        카테고리_조회_응답_데이터(JPA_카테고리_ID, "JPA", 하위_카테고리들(
                                카테고리_조회_응답_데이터(N1_카테고리_ID, "N + 1", 비어있음())
                        )),
                        카테고리_조회_응답_데이터(Security_카테고리_ID, "Security", 하위_카테고리들(
                                카테고리_조회_응답_데이터(OAuth_카테고리_ID, "OAuth", 비어있음()),
                                카테고리_조회_응답_데이터(CSRF_카테고리_ID, "CSRF", 비어있음())
                        ))
                )),
                카테고리_조회_응답_데이터(Algorithm_카테고리_ID, "Algorithm", 하위_카테고리들(
                        카테고리_조회_응답_데이터(DFS_카테고리_ID, "DFS", 비어있음())
                ))
        );

        // when
        var 응답 = 블로그의_카테고리_조회_요청(말랑_블로그_ID);

        // then
        카테고리_조회_응답을_검증한다(응답, 예상_응답);
    }

    @Nested
    class 카테고리_제거_시 {

        @Test
        void 카테고리는_제거되며_해당_카테고리를_가진_포스트는_카테고리_없음_상태가_된다() {
            // given
            var Spring_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "Spring", 없음());
            var JPA_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "JPA", Spring_카테고리_ID);
            var 포스트_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_ID,
                    "제목",
                    "내용",
                    없음(),
                    "인트로",
                    PUBLIC,
                    없음(),
                    JPA_카테고리_ID
            );

            // when
            var 응답 = 카테고리_제거_요청(말랑_세션_ID, JPA_카테고리_ID);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
            포스트_내용_검증(포스트_ID, 포스트_단일_조회_데이터(포스트_ID, "말랑",
                    없음(), 없음(),
                    "제목", "내용",
                    null,
                    PUBLIC, 보호되지_않음, 좋아요_안눌림, 0));
        }

        @Test
        void 하위_카테고리가_있다면_제거할_수_없다() {
            // given
            var Spring_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "Spring", 없음());
            카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "JPA", Spring_카테고리_ID);

            // when
            var 응답 = 카테고리_제거_요청(말랑_세션_ID, Spring_카테고리_ID);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
        }

        @Test
        void 자신의_카테고리가_아니라면_제거할_수_없다() {
            // given
            var Spring_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "Spring", 없음());

            // when
            var 응답 = 카테고리_제거_요청(동훈_세션_ID, Spring_카테고리_ID);

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
        }
    }
}
