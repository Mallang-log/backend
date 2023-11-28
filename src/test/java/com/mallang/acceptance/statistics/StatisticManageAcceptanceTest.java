package com.mallang.acceptance.statistics;


import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.인증되지_않음;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static com.mallang.acceptance.statistics.StatisticManageAcceptanceSteps.포스트_통계_조회_요청;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_24_금;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_25_토;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_26_일;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_27_월;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_28_화;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static com.mallang.statistics.query.support.PeriodType.DAY;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.post.domain.PostId;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.statistics.query.response.PostViewStatisticResponse;
import com.mallang.statistics.statistic.PostViewStatistic;
import com.mallang.statistics.statistic.PostViewStatisticRepository;
import io.restassured.common.mapper.TypeRef;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("통계 관리 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StatisticManageAcceptanceTest extends AcceptanceTest {

    @Autowired
    private PostViewStatisticRepository postViewStatisticRepository;

    private final String 말랑_블로그_이름 = "mallang-log";
    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private Long 말랑_블로그_ID;
    private Long 포스트_ID;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        CreatePostRequest 포스트_요청 = new CreatePostRequest(
                말랑_블로그_이름,
                "[공개] 제목",
                "[공개] 내용",
                "[공개] 섬네일",
                "[공개] 포스트 인트로 입니다.",
                PUBLIC,
                null,
                null,
                Collections.emptyList()
        );
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        말랑_블로그_ID = ID를_추출한다(블로그_개설_요청(말랑_세션_ID, "mallang-log"));
        포스트_ID = 포스트_생성(말랑_세션_ID, 포스트_요청);
    }

    @Nested
    class 포스트_통계_조회_API {

        @Test
        void 포스트_통계_조회() {
            // given
            PostId postId = new PostId(포스트_ID, 말랑_블로그_ID);
            var 통계_2023_11_25 = new PostViewStatistic(날짜_2023_11_25_토, postId, 10);
            var 통계_2023_11_26 = new PostViewStatistic(날짜_2023_11_26_일, postId, 5);
            var 통계_2023_11_27 = new PostViewStatistic(날짜_2023_11_27_월, postId);
            var 통계_2023_11_28 = new PostViewStatistic(날짜_2023_11_28_화, postId, 100);
            postViewStatisticRepository.saveAll(List.of(통계_2023_11_25, 통계_2023_11_26, 통계_2023_11_27, 통계_2023_11_28));
            var date_2023_11_28 = "2023-11-28";

            // when
            var 응답 = 포스트_통계_조회_요청(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    포스트_ID,
                    DAY,
                    date_2023_11_28,
                    5
            );

            // then
            List<PostViewStatisticResponse> response = 응답.as(new TypeRef<>() {
            });
            assertThat(response).usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new PostViewStatisticResponse(날짜_2023_11_24_금, 날짜_2023_11_24_금, 0),
                            new PostViewStatisticResponse(날짜_2023_11_25_토, 날짜_2023_11_25_토, 10),
                            new PostViewStatisticResponse(날짜_2023_11_26_일, 날짜_2023_11_26_일, 5),
                            new PostViewStatisticResponse(날짜_2023_11_27_월, 날짜_2023_11_27_월, 0),
                            new PostViewStatisticResponse(날짜_2023_11_28_화, 날짜_2023_11_28_화, 100)
                    ));
        }

        @Test
        void 자신의_포스트가_아닌_경우_볼_수_없다() {
            // when
            var date_2023_11_28 = "2023-11-28";
            var 응답 = 포스트_통계_조회_요청(
                    동훈_세션_ID,
                    말랑_블로그_이름,
                    포스트_ID,
                    DAY,
                    date_2023_11_28,
                    5
            );
            var 응답2 = 포스트_통계_조회_요청(
                    null,
                    말랑_블로그_이름,
                    포스트_ID,
                    DAY,
                    date_2023_11_28,
                    5
            );

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
            응답_상태를_검증한다(응답2, 인증되지_않음);
        }
    }
}
