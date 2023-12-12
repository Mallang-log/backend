package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.잘못된_요청;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.내_정보_조회_요청;
import static com.mallang.acceptance.post.StarGroupAcceptanceSteps.즐겨찾기_그룹_계층구조_수정_요청;
import static com.mallang.acceptance.post.StarGroupAcceptanceSteps.즐겨찾기_그룹_생성_요청;
import static com.mallang.acceptance.post.StarGroupAcceptanceSteps.즐겨찾기_그룹_이름_수정_요청;
import static com.mallang.acceptance.post.StarGroupAcceptanceSteps.즐겨찾기_그룹_제거_요청;
import static com.mallang.acceptance.post.StarGroupAcceptanceSteps.회원의_즐겨찾기_그룹_목록_조회_요청;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.auth.query.response.MemberResponse;
import com.mallang.post.presentation.request.CreateStarGroupRequest;
import com.mallang.post.query.response.StarGroupListResponse;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("즐겨찾기 그룹 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StarGroupAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private CreateStarGroupRequest Spring_즐겨찾기_그룹_생성_요청;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        Spring_즐겨찾기_그룹_생성_요청 = new CreateStarGroupRequest(
                "Spring",
                null,
                null,
                null
        );
    }

    @Nested
    class 즐겨찾기_그룹_생성_API {

        @Test
        void 즐겨찾기_그룹를_생성한다() {
            // when
            var 응답 = 즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            값이_존재한다(ID를_추출한다(응답));
        }

        @Test
        void 하위_즐겨찾기_그룹를_생성한다() {
            // given
            var 상위_즐겨찾기_그룹_생성_응답 = 즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청);
            var 상위_즐겨찾기_그룹_ID = ID를_추출한다(상위_즐겨찾기_그룹_생성_응답);
            var JPA_즐겨찾기_그룹_생성_요청 = new CreateStarGroupRequest(
                    "Jpa",
                    상위_즐겨찾기_그룹_ID,
                    null,
                    null
            );

            // when
            var 응답 = 즐겨찾기_그룹_생성_요청(말랑_세션_ID, JPA_즐겨찾기_그룹_생성_요청);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            값이_존재한다(ID를_추출한다(응답));
        }

        @Test
        void 타인의_즐겨찾기_그룹_계층에_참여하려는_경우_예외() {
            // given
            var 상위_즐겨찾기_그룹_생성_응답 = 즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청);
            var 상위_즐겨찾기_그룹_ID = ID를_추출한다(상위_즐겨찾기_그룹_생성_응답);
            var JPA_즐겨찾기_그룹_생성_요청 = new CreateStarGroupRequest(
                    "Jpa",
                    상위_즐겨찾기_그룹_ID,
                    null,
                    null
            );

            // when
            var 응답 = 즐겨찾기_그룹_생성_요청(동훈_세션_ID, JPA_즐겨찾기_그룹_생성_요청);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 즐겨찾기_그룹_계층구조_수정_API {

        @Test
        void 즐겨찾기_그룹_게층구조를_업데이트한다() {
            // given
            var Spring_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청));
            var NODE_즐겨찾기_그룹_생성_요청 = new CreateStarGroupRequest(
                    "NODE",
                    null,
                    Spring_즐겨찾기_그룹_ID,
                    null
            );
            var NODE_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, NODE_즐겨찾기_그룹_생성_요청));

            // when
            var 응답 = 즐겨찾기_그룹_계층구조_수정_요청(
                    말랑_세션_ID,
                    NODE_즐겨찾기_그룹_ID,
                    null,
                    null,
                    Spring_즐겨찾기_그룹_ID
            );

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 자신의_즐겨찾기_그룹가_아니라면_수정할_수_없다() {
            // given
            var Spring_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청));
            var NODE_즐겨찾기_그룹_생성_요청 = new CreateStarGroupRequest(
                    "NODE",
                    null,
                    null,
                    Spring_즐겨찾기_그룹_ID
            );
            var NODE_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, NODE_즐겨찾기_그룹_생성_요청));

            // when
            var 응답 = 즐겨찾기_그룹_계층구조_수정_요청(
                    동훈_세션_ID,
                    NODE_즐겨찾기_그룹_ID,
                    null,
                    Spring_즐겨찾기_그룹_ID,
                    null
            );

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 타인의_즐겨찾기_그룹_계층으로_옮길_수_없다() {
            // given
            var Spring_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청));
            var NODE_즐겨찾기_그룹_생성_요청 = new CreateStarGroupRequest(
                    "NODE",
                    null,
                    null,
                    null
            );
            var NODE_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(동훈_세션_ID, NODE_즐겨찾기_그룹_생성_요청));

            // when
            var 응답 = 즐겨찾기_그룹_계층구조_수정_요청(
                    동훈_세션_ID,
                    NODE_즐겨찾기_그룹_ID,
                    null,
                    Spring_즐겨찾기_그룹_ID,
                    null
            );

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 즐겨찾기_그룹_이름_수정_API {

        @Test
        void 즐겨찾기_그룹_이름을_업데이트한다() {
            // given
            var Spring_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청));

            // when
            var 응답 = 즐겨찾기_그룹_이름_수정_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_ID, "Node");

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 자신의_즐겨찾기_그룹가_아니라면_수정할_수_없다() {
            // given
            var Spring_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청));

            // when
            var 응답 = 즐겨찾기_그룹_이름_수정_요청(동훈_세션_ID, Spring_즐겨찾기_그룹_ID, "Node");

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 즐겨찾기_그룹_삭제_API {

        private Long Spring_즐겨찾기_그룹_ID;
        private Long JPA_즐겨찾기_그룹_ID;

        @BeforeEach
        void setUp() {
            Spring_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청));
            var JPA_즐겨찾기_그룹_생성_요청 = new CreateStarGroupRequest(
                    "JPA",
                    Spring_즐겨찾기_그룹_ID,
                    null,
                    null
            );
            JPA_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, JPA_즐겨찾기_그룹_생성_요청));
        }

        @Test
        void 하위_즐겨찾기_그룹이_있다면_제거할_수_없다() {
            // when
            var 응답 = 즐겨찾기_그룹_제거_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_ID);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
        }

        @Test
        void 자신의_즐겨찾기_그룹이_아니라면_제거할_수_없다() {
            // when
            var 응답 = 즐겨찾기_그룹_제거_요청(동훈_세션_ID, JPA_즐겨찾기_그룹_ID);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 자신의_즐겨찾기_그룹이며_하위_즐겨찾기_그룹이_없다면_제거할_수_있다() {
            // when
            var 응답 = 즐겨찾기_그룹_제거_요청(말랑_세션_ID, JPA_즐겨찾기_그룹_ID);

            // then
            응답_상태를_검증한다(응답, 본문_없음);
        }

        // TODO 즐겨찾기 그룹 제거 시, 해당 그룹에 속한 즐겨찾기 글들은 그룹 없음으로 변환
    }

    @Nested
    class 즐겨찾기_그룹_조회_API {

        @Test
        void 특정_블로그의_즐겨찾기_그룹를_조회한다() {
            // given
            var 말랑_ID = 내_정보_조회_요청(말랑_세션_ID)
                    .as(MemberResponse.class)
                    .id();
            var 다른사람_NODE_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(
                    동훈_세션_ID,
                    "Node",
                    null,
                    null,
                    null
            ));
            var Spring_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청));
            var JPA_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, new CreateStarGroupRequest(
                    "JPA",
                    Spring_즐겨찾기_그룹_ID,
                    null,
                    null
            )));
            var N1_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, new CreateStarGroupRequest(
                    "N + 1",
                    JPA_즐겨찾기_그룹_ID,
                    null,
                    null
            )));
            var Security_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, new CreateStarGroupRequest(
                    "Security",
                    Spring_즐겨찾기_그룹_ID,
                    null,
                    JPA_즐겨찾기_그룹_ID
            )));
            var OAuth_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, new CreateStarGroupRequest(
                    "OAuth",
                    Security_즐겨찾기_그룹_ID,
                    null,
                    null
            )));
            var CSRF_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, new CreateStarGroupRequest(
                    "CSRF",
                    Security_즐겨찾기_그룹_ID,
                    null,
                    OAuth_즐겨찾기_그룹_ID
            )));
            var Algorithm_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, new CreateStarGroupRequest(
                    "Algorithm",
                    없음(),
                    null,
                    Spring_즐겨찾기_그룹_ID
            )));
            var DFS_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, new CreateStarGroupRequest(
                    "DFS",
                    Algorithm_즐겨찾기_그룹_ID,
                    null,
                    null
            )));
            var 예상_응답 = List.of(
                    new StarGroupListResponse(Algorithm_즐겨찾기_그룹_ID, "Algorithm", List.of(
                            new StarGroupListResponse(DFS_즐겨찾기_그룹_ID, "DFS", emptyList())
                    )),
                    new StarGroupListResponse(Spring_즐겨찾기_그룹_ID, "Spring", List.of(
                            new StarGroupListResponse(Security_즐겨찾기_그룹_ID, "Security", List.of(
                                    new StarGroupListResponse(CSRF_즐겨찾기_그룹_ID, "CSRF", emptyList()),
                                    new StarGroupListResponse(OAuth_즐겨찾기_그룹_ID, "OAuth", emptyList())
                            )),
                            new StarGroupListResponse(JPA_즐겨찾기_그룹_ID, "JPA", List.of(
                                    new StarGroupListResponse(N1_즐겨찾기_그룹_ID, "N + 1", emptyList())
                            ))
                    ))
            );

            // when
            var 응답 = 회원의_즐겨찾기_그룹_목록_조회_요청(말랑_ID);

            // then
            List<StarGroupListResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses)
                    .usingRecursiveComparison()
                    .isEqualTo(예상_응답);
        }
    }
}




