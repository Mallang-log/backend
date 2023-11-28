package com.mallang.acceptance.statistics;


import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static com.mallang.acceptance.statistics.StatisticAcceptanceSteps.블로그_방문자_통계_조회_요청;
import static com.mallang.acceptance.statistics.StatisticAcceptanceSteps.포스트_누적_조회수_조회_요청;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_25_토;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_26_일;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_27_월;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_28_화;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.post.domain.PostId;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.statistics.api.query.response.BlogVisitStatisticSimpleResponse;
import com.mallang.statistics.api.query.response.PostTotalViewsResponse;
import com.mallang.statistics.statistic.BlogVisitStatistic;
import com.mallang.statistics.statistic.BlogVisitStatisticRepository;
import com.mallang.statistics.statistic.PostViewStatistic;
import com.mallang.statistics.statistic.PostViewStatisticRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("일반 사용자용 통계 조회 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StatisticAcceptanceTest extends AcceptanceTest {

    @Autowired
    private PostViewStatisticRepository postViewStatisticRepository;

    @Autowired
    private BlogVisitStatisticRepository blogVisitStatisticRepository;

    private final String 말랑_블로그_이름 = "mallang-log";
    private String 말랑_세션_ID;
    private Long 말랑_블로그_ID;
    private Long 포스트_ID;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        말랑_블로그_ID = ID를_추출한다(블로그_개설_요청(말랑_세션_ID, "mallang-log"));
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
        포스트_ID = 포스트_생성(말랑_세션_ID, 포스트_요청);
    }

    @Nested
    class 블로그_방문자_통계_조회_API {

        @Test
        void 블로그_통계_조회() {
            // given
            var 통계_2023_11_25 = new BlogVisitStatistic(날짜_2023_11_25_토, 말랑_블로그_이름, 200);
            var 통계_2023_11_26 = new BlogVisitStatistic(날짜_2023_11_26_일, 말랑_블로그_이름, 5);
            var 통계_2023_11_27 = new BlogVisitStatistic(날짜_2023_11_27_월, 말랑_블로그_이름, 3);
            var 통계_2023_11_28 = new BlogVisitStatistic(날짜_2023_11_28_화, 말랑_블로그_이름, 20);
            blogVisitStatisticRepository.saveAll(List.of(통계_2023_11_25, 통계_2023_11_26, 통계_2023_11_27, 통계_2023_11_28));
            var date_2023_11_28 = "2023-11-28";

            // when
            var 응답 = 블로그_방문자_통계_조회_요청(
                    말랑_블로그_이름,
                    date_2023_11_28
            );

            // then
            BlogVisitStatisticSimpleResponse response = 응답.as(BlogVisitStatisticSimpleResponse.class);
            assertThat(response.totalVisitCount()).isEqualTo(228);
            assertThat(response.todayVisitCount()).isEqualTo(20);
            assertThat(response.yesterdayVisitCount()).isEqualTo(3);
        }
    }

    @Nested
    class 포스트_누적_조회수_조회_API {

        @Test
        void 포스트_누적_조회수_조회() {
            // given
            var postId = new PostId(포스트_ID, 말랑_블로그_ID);
            var 통계_2023_11_25 = new PostViewStatistic(날짜_2023_11_25_토, postId, 10);
            var 통계_2023_11_26 = new PostViewStatistic(날짜_2023_11_26_일, postId, 5);
            var 통계_2023_11_27 = new PostViewStatistic(날짜_2023_11_27_월, postId);
            var 통계_2023_11_28 = new PostViewStatistic(날짜_2023_11_28_화, postId, 100);
            postViewStatisticRepository.saveAll(List.of(통계_2023_11_25, 통계_2023_11_26, 통계_2023_11_27, 통계_2023_11_28));

            // when
            var 응답 = 포스트_누적_조회수_조회_요청(말랑_블로그_이름, 포스트_ID);

            // then
            PostTotalViewsResponse response = 응답.as(PostTotalViewsResponse.class);
            assertThat(response.totalViewCount()).isEqualTo(115);
        }
    }
}
