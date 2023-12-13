package com.mallang.acceptance.reference;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.잘못된_요청;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.AcceptanceSteps.중복됨;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.reference.LabelAcceptanceSteps.나의_라벨_조회_요청;
import static com.mallang.acceptance.reference.LabelAcceptanceSteps.라벨_계층구조_수정_요청;
import static com.mallang.acceptance.reference.LabelAcceptanceSteps.라벨_생성_요청;
import static com.mallang.acceptance.reference.LabelAcceptanceSteps.라벨_속성_수정_요청;
import static com.mallang.acceptance.reference.LabelAcceptanceSteps.라벨_제거_요청;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.reference.presentation.request.CreateLabelRequest;
import com.mallang.reference.presentation.request.UpdateLabelAttributeRequest;
import com.mallang.reference.presentation.request.UpdateLabelHierarchyRequest;
import com.mallang.reference.query.response.LabelListResponse;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("라벨 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class LabelAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private CreateLabelRequest Spring_라벨_생성_요청;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        Spring_라벨_생성_요청 = new CreateLabelRequest(
                "Spring",
                "#000000",
                null,
                null
        );
    }

    @Nested
    class 라벨_생성_API {

        @Test
        void 라벨을_생성한다() {
            // when
            var 응답 = 라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            값이_존재한다(ID를_추출한다(응답));
        }

        @Test
        void 색상_코드_형식이_잘못된_경우_예외() {
            // when
            var 잘못된_색상_요청 = new CreateLabelRequest(
                    "Spring",
                    "000000",
                    null,
                    null
            );
            var 응답 = 라벨_생성_요청(말랑_세션_ID, 잘못된_색상_요청);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
        }

        @Test
        void 이미_라벨이_존재하는데_부모와_형제와의_관계가_모두_주어지지_않은_라벨_생성_시_예외() {
            // given
            라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청);
            var 다른_루트_라벨_생성_요청 = new CreateLabelRequest(
                    "Other",
                    "#000000",
                    null,
                    null
            );

            // when
            var 응답 = 라벨_생성_요청(말랑_세션_ID, 다른_루트_라벨_생성_요청);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
        }

        @Test
        void 형제_라벨을_생성한다() {
            // given
            var 상위_라벨_생성_응답 = 라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청);
            var 상위_라벨_ID = ID를_추출한다(상위_라벨_생성_응답);
            var JPA_라벨_생성_요청 = new CreateLabelRequest(
                    "Jpa",
                    "#000000",
                    상위_라벨_ID,
                    null
            );

            // when
            var 응답 = 라벨_생성_요청(말랑_세션_ID, JPA_라벨_생성_요청);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            값이_존재한다(ID를_추출한다(응답));
        }

        @Test
        void 형제_라벨_생성_시_이름이_중복되면_예외() {
            // given
            var 상위_라벨_생성_응답 = 라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청);
            var 상위_라벨_ID = ID를_추출한다(상위_라벨_생성_응답);
            var JPA_라벨_생성_요청 = new CreateLabelRequest(
                    "Spring",
                    "#000000",
                    상위_라벨_ID,
                    null
            );

            // when
            var 응답 = 라벨_생성_요청(말랑_세션_ID, JPA_라벨_생성_요청);

            // then
            응답_상태를_검증한다(응답, 중복됨);
        }

        @Test
        void 타인의_라벨_계층에_참여하려는_경우_예외() {
            // given
            var 상위_라벨_생성_응답 = 라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청);
            var 상위_라벨_ID = ID를_추출한다(상위_라벨_생성_응답);
            var JPA_라벨_생성_요청 = new CreateLabelRequest(
                    "JPA",
                    "#000000",
                    상위_라벨_ID,
                    null
            );

            // when
            var 응답 = 라벨_생성_요청(동훈_세션_ID, JPA_라벨_생성_요청);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 라벨_계층구조_수정_API {

        @Test
        void 라벨_게층구조를_업데이트한다() {
            // given
            var Spring_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청));
            var NODE_라벨_생성_요청 = new CreateLabelRequest(
                    "NODE",
                    "#000000",
                    Spring_라벨_ID,
                    null
            );
            var NODE_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, NODE_라벨_생성_요청));
            var 계층구조_수정_요청 = new UpdateLabelHierarchyRequest(null, Spring_라벨_ID);

            // when
            var 응답 = 라벨_계층구조_수정_요청(
                    말랑_세션_ID,
                    NODE_라벨_ID,
                    계층구조_수정_요청
            );

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 자신의_라벨이_아니라면_수정할_수_없다() {
            // given
            var Spring_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청));
            var NODE_라벨_생성_요청 = new CreateLabelRequest(
                    "NODE",
                    "#000000",
                    Spring_라벨_ID,
                    null
            );
            var NODE_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, NODE_라벨_생성_요청));
            var 계층구조_수정_요청 = new UpdateLabelHierarchyRequest(null, Spring_라벨_ID);

            // when
            var 응답 = 라벨_계층구조_수정_요청(
                    동훈_세션_ID,
                    NODE_라벨_ID,
                    계층구조_수정_요청
            );

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 타인의_라벨_계층으로_옮길_수_없다() {
            // given
            var Spring_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청));
            var NODE_라벨_생성_요청 = new CreateLabelRequest(
                    "NODE",
                    "#000000",
                    null,
                    null
            );
            var NODE_라벨_ID = ID를_추출한다(라벨_생성_요청(동훈_세션_ID, NODE_라벨_생성_요청));
            var 계층구조_수정_요청 = new UpdateLabelHierarchyRequest(null, Spring_라벨_ID);

            // when
            var 응답 = 라벨_계층구조_수정_요청(
                    말랑_세션_ID,
                    NODE_라벨_ID,
                    계층구조_수정_요청
            );

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 라벨_속성_수정_API {

        @Test
        void 자신의_라벨이_아니라면_수정할_수_없다() {
            // given
            var Spring_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청));
            var 라벨_속성_수정_요청 = new UpdateLabelAttributeRequest("수정", "#ffffff");

            // when
            var 응답 = 라벨_속성_수정_요청(동훈_세션_ID, Spring_라벨_ID, 라벨_속성_수정_요청);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 라벨_이름과_색상을_업데이트한다() {
            // given
            var Spring_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청));
            var 라벨_속성_수정_요청 = new UpdateLabelAttributeRequest("수정", "#ffffff");

            // when
            var 응답 = 라벨_속성_수정_요청(말랑_세션_ID, Spring_라벨_ID, 라벨_속성_수정_요청);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 이름이_중복되면_예외() {
            // given
            var Spring_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청));
            var Node_라벨_생성_요청 = new CreateLabelRequest(
                    "Node",
                    "#000000",
                    null,
                    Spring_라벨_ID
            );
            라벨_생성_요청(말랑_세션_ID, Node_라벨_생성_요청);
            var 라벨_수정_요청 = new UpdateLabelAttributeRequest("Node", "#ffffff");

            // when
            var 응답 = 라벨_속성_수정_요청(말랑_세션_ID, Spring_라벨_ID, 라벨_수정_요청);

            // then
            응답_상태를_검증한다(응답, 중복됨);
        }

        @Test
        void 동일한_이름으로_수정하는_경우_예외가_발생하지_않는다() {
            // given
            var Spring_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청));
            var 기존과_동일한_이름_라벨_수정_요청 = new UpdateLabelAttributeRequest("Spring", "#ffffff");

            // when
            var 응답 = 라벨_속성_수정_요청(말랑_세션_ID, Spring_라벨_ID, 기존과_동일한_이름_라벨_수정_요청);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 색상_형식이_잘못되면_예외() {
            // given
            var Spring_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청));
            var 라벨_수정_요청 = new UpdateLabelAttributeRequest("Spring", "#fff");

            // when
            var 응답 = 라벨_속성_수정_요청(말랑_세션_ID, Spring_라벨_ID, 라벨_수정_요청);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
        }
    }

    @Nested
    class 라벨_삭제_API {

        private Long Spring_라벨_ID;

        @BeforeEach
        void setUp() {
            Spring_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청));
        }

        @Test
        void 라벨은_제거되며_해당_라벨을_가진_링크는_라벨_없음_상태가_된다() {
            // when
            var 응답 = 라벨_제거_요청(말랑_세션_ID, Spring_라벨_ID);

            // then
            응답_상태를_검증한다(응답, 본문_없음);
        }

        @Test
        void 자신의_라벨이_아니라면_제거할_수_없다() {
            // when
            var 응답 = 라벨_제거_요청(동훈_세션_ID, Spring_라벨_ID);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 라벨_조회_API {

        @Test
        void 특정_블로그의_라벨을_조회한다() {
            // given
            var NODE_라벨 = new CreateLabelRequest("Node", "#000000", null, null);
            Long 다른사람_NODE_라벨_ID = ID를_추출한다(라벨_생성_요청(동훈_세션_ID, NODE_라벨));

            var Spring_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, Spring_라벨_생성_요청));
            var JPA_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, new CreateLabelRequest(
                    "JPA",
                    "#000000",
                    Spring_라벨_ID,
                    null
            )));
            var Security_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, new CreateLabelRequest(
                    "Security",
                    "#000000",
                    null,
                    Spring_라벨_ID
            )));
            var OAuth_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션_ID, new CreateLabelRequest(
                    "OAuth",
                    "#000000",
                    Spring_라벨_ID,
                    JPA_라벨_ID
            )));
            var 예상_응답 = List.of(
                    new LabelListResponse(Security_라벨_ID, "Security", "#000000", null, Spring_라벨_ID),
                    new LabelListResponse(Spring_라벨_ID, "Spring", "#000000", Security_라벨_ID, OAuth_라벨_ID),
                    new LabelListResponse(OAuth_라벨_ID, "OAuth", "#000000", Spring_라벨_ID, JPA_라벨_ID),
                    new LabelListResponse(JPA_라벨_ID, "JPA", "#000000", OAuth_라벨_ID, null)
            );

            // when
            var 응답 = 나의_라벨_조회_요청(말랑_세션_ID);

            // then
            List<LabelListResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses).usingRecursiveComparison()
                    .isEqualTo(예상_응답);
        }
    }
}
