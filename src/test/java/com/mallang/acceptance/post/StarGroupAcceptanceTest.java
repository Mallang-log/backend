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
import static com.mallang.acceptance.AcceptanceSteps.중복됨;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.내_정보_조회_요청;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.공개_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static com.mallang.acceptance.post.PostStarAcceptanceSteps.특정_회원의_즐겨찾기_포스트_목록_조회_요청;
import static com.mallang.acceptance.post.PostStarAcceptanceSteps.포스트_즐겨찾기_요청;
import static com.mallang.acceptance.post.StarGroupAcceptanceSteps.즐겨찾기_그룹_계층구조_수정_요청;
import static com.mallang.acceptance.post.StarGroupAcceptanceSteps.즐겨찾기_그룹_생성_요청;
import static com.mallang.acceptance.post.StarGroupAcceptanceSteps.즐겨찾기_그룹_이름_수정_요청;
import static com.mallang.acceptance.post.StarGroupAcceptanceSteps.즐겨찾기_그룹_제거_요청;
import static com.mallang.acceptance.post.StarGroupAcceptanceSteps.회원의_즐겨찾기_그룹_목록_조회_요청;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.auth.query.response.MemberResponse;
import com.mallang.common.presentation.PageResponse;
import com.mallang.post.presentation.request.CreateStarGroupRequest;
import com.mallang.post.query.response.StarGroupListResponse;
import com.mallang.post.query.response.StaredPostResponse;
import com.mallang.post.query.response.StaredPostResponse.StarGroupResponse;
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
        void 이미_즐겨찾기_그룹이_존재하는데_부모와_형제와의_관계가_모두_주어지지_않은_그룹_생성_시_예외() {
            // given
            즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청);
            var 다른_루트_그룹_생성_요청 = new CreateStarGroupRequest(
                    "Other",
                    null,
                    null,
                    null
            );

            // when
            var 응답 = 즐겨찾기_그룹_생성_요청(말랑_세션_ID, 다른_루트_그룹_생성_요청);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
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

        @Test
        void 형제와_이름이_중복되면_예외() {
            // given
            var 그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청));
            var 형제_생성_요청 = new CreateStarGroupRequest(
                    "Spring",
                    null,
                    그룹_ID,
                    null
            );

            // when
            var 응답 = 즐겨찾기_그룹_생성_요청(말랑_세션_ID, 형제_생성_요청);

            // then
            응답_상태를_검증한다(응답, 중복됨);
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

        @Test
        void 형제와_이름이_중복되면_예외() {
            // given
            var Spring_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_생성_요청));
            var Spring_하위_즐겨찾기_그룹_생성_요청 = new CreateStarGroupRequest(
                    "Spring",
                    Spring_즐겨찾기_그룹_ID,
                    null,
                    null
            );
            var Spring_하위_즐겨찾기_그룹_ID = ID를_추출한다(즐겨찾기_그룹_생성_요청(말랑_세션_ID, Spring_하위_즐겨찾기_그룹_생성_요청));

            // when
            var 응답 = 즐겨찾기_그룹_계층구조_수정_요청(
                    말랑_세션_ID,
                    Spring_하위_즐겨찾기_그룹_ID,
                    null,
                    Spring_즐겨찾기_그룹_ID,
                    null
            );

            // then
            응답_상태를_검증한다(응답, 중복됨);
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
        void 형제와_이름이_중복되면_예외() {
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
            var 응답 = 즐겨찾기_그룹_이름_수정_요청(말랑_세션_ID, Spring_즐겨찾기_그룹_ID, "NODE");

            // then
            응답_상태를_검증한다(응답, 중복됨);
        }

        @Test
        void 자신의_즐겨찾기_그룹이_아니라면_수정할_수_없다() {
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

        @Test
        void 해당_그룹에_속한_즐겨찾기된_포스트들을_그룹_없음으로_만든다() {
            // given
            String 말랑_블로그_이름 = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_이름));
            포스트_즐겨찾기_요청(말랑_세션_ID, 포스트1_ID, 말랑_블로그_이름, JPA_즐겨찾기_그룹_ID, null);
            포스트_즐겨찾기_요청(말랑_세션_ID, 포스트2_ID, 말랑_블로그_이름, JPA_즐겨찾기_그룹_ID, null);
            포스트_즐겨찾기_요청(말랑_세션_ID, 포스트3_ID, 말랑_블로그_이름, Spring_즐겨찾기_그룹_ID, null);

            // when
            즐겨찾기_그룹_제거_요청(말랑_세션_ID, JPA_즐겨찾기_그룹_ID);

            // then
            var 말랑_ID = 내_정보_조회_요청(말랑_세션_ID)
                    .as(MemberResponse.class)
                    .id();
            PageResponse<StaredPostResponse> responses = 특정_회원의_즐겨찾기_포스트_목록_조회_요청(말랑_세션_ID, 말랑_ID, null)
                    .as(new TypeRef<>() {
                    });
            assertThat(responses.content())
                    .extracting(StaredPostResponse::starGroup)
                    .extracting(StarGroupResponse::starGroupId)
                    .containsExactly(Spring_즐겨찾기_그룹_ID, null, null);
        }
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
                    new StarGroupListResponse(
                            Algorithm_즐겨찾기_그룹_ID,
                            "Algorithm",
                            null,
                            null,
                            Spring_즐겨찾기_그룹_ID,
                            List.of(
                                    new StarGroupListResponse(
                                            DFS_즐겨찾기_그룹_ID,
                                            "DFS",
                                            Algorithm_즐겨찾기_그룹_ID,
                                            null,
                                            null,
                                            emptyList()
                                    )
                            )),
                    new StarGroupListResponse(
                            Spring_즐겨찾기_그룹_ID,
                            "Spring",
                            null,
                            Algorithm_즐겨찾기_그룹_ID,
                            null,
                            List.of(
                                    new StarGroupListResponse(
                                            Security_즐겨찾기_그룹_ID,
                                            "Security",
                                            Spring_즐겨찾기_그룹_ID,
                                            null,
                                            JPA_즐겨찾기_그룹_ID,
                                            List.of(
                                                    new StarGroupListResponse(
                                                            CSRF_즐겨찾기_그룹_ID,
                                                            "CSRF",
                                                            Security_즐겨찾기_그룹_ID,
                                                            null,
                                                            OAuth_즐겨찾기_그룹_ID,
                                                            emptyList()
                                                    ),
                                                    new StarGroupListResponse(
                                                            OAuth_즐겨찾기_그룹_ID,
                                                            "OAuth",
                                                            Security_즐겨찾기_그룹_ID,
                                                            CSRF_즐겨찾기_그룹_ID,
                                                            null,
                                                            emptyList()
                                                    )
                                            )),
                                    new StarGroupListResponse(
                                            JPA_즐겨찾기_그룹_ID,
                                            "JPA",
                                            Spring_즐겨찾기_그룹_ID,
                                            Security_즐겨찾기_그룹_ID,
                                            null,
                                            List.of(
                                                    new StarGroupListResponse(
                                                            N1_즐겨찾기_그룹_ID,
                                                            "N + 1",
                                                            JPA_즐겨찾기_그룹_ID,
                                                            null,
                                                            null,
                                                            emptyList()
                                                    )
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




