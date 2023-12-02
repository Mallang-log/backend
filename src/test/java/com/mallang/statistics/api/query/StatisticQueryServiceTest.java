package com.mallang.statistics.api.query;

import static com.mallang.common.LocalDateFixture.날짜_2020_12_31;
import static com.mallang.common.LocalDateFixture.날짜_2020_1_1;
import static com.mallang.common.LocalDateFixture.날짜_2021_12_31;
import static com.mallang.common.LocalDateFixture.날짜_2021_1_1;
import static com.mallang.common.LocalDateFixture.날짜_2022_12_31;
import static com.mallang.common.LocalDateFixture.날짜_2022_1_1;
import static com.mallang.common.LocalDateFixture.날짜_2023_10_1;
import static com.mallang.common.LocalDateFixture.날짜_2023_10_20;
import static com.mallang.common.LocalDateFixture.날짜_2023_10_31;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_13_월;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_15_수;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_19_일;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_1_수;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_20_월;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_21_화;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_25_토;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_26_일;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_27_월;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_28_화;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_30_목;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_5_일;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_6_월;
import static com.mallang.common.LocalDateFixture.날짜_2023_12_31;
import static com.mallang.common.LocalDateFixture.날짜_2023_12_3_일;
import static com.mallang.common.LocalDateFixture.날짜_2023_1_1;
import static com.mallang.common.LocalDateFixture.날짜_2023_9_1;
import static com.mallang.common.LocalDateFixture.날짜_2023_9_30;
import static com.mallang.statistics.api.query.PeriodType.DAY;
import static com.mallang.statistics.api.query.PeriodType.MONTH;
import static com.mallang.statistics.api.query.PeriodType.WEEK;
import static com.mallang.statistics.api.query.PeriodType.YEAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.common.ServiceTest;
import com.mallang.post.domain.PostId;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.exception.NotFoundPostException;
import com.mallang.statistics.api.presentation.support.StatisticQueryConditionConverter;
import com.mallang.statistics.api.query.response.BlogVisitStatisticManageResponse;
import com.mallang.statistics.api.query.response.BlogVisitStatisticSimpleResponse;
import com.mallang.statistics.api.query.response.PostTotalViewsResponse;
import com.mallang.statistics.api.query.response.PostViewStatisticResponse;
import com.mallang.statistics.statistic.BlogVisitStatistic;
import com.mallang.statistics.statistic.BlogVisitStatisticRepository;
import com.mallang.statistics.statistic.PostViewStatistic;
import com.mallang.statistics.statistic.PostViewStatisticRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("통계 조회 서비스 (StatisticQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StatisticQueryServiceTest extends ServiceTest {

    @Autowired
    private BlogVisitStatisticRepository blogVisitStatisticRepository;

    @Autowired
    private StatisticQueryService statisticQueryService;

    @Autowired
    private PostViewStatisticRepository postViewStatisticRepository;

    @Nested
    class 블로그_주인용_블로그_방문_통계_조회_시 {

        private Long memberId;
        private String blogName;

        @BeforeEach
        void setUp() {
            memberId = 회원을_저장한다("말랑");
            blogName = 블로그_개설(memberId, "mallang-log");
        }

        @Test
        void 날짜가_잘못된_경우() {
            // given
            StatisticQueryCondition cond = new StatisticQueryCondition(DAY, 날짜_2023_11_6_월, 날짜_2023_11_5_일);

            // when
            List<BlogVisitStatisticManageResponse> result = statisticQueryService.getBlogVisitStatistics(
                    memberId,
                    blogName,
                    cond
            );

            // then
            assertThat(result).isEmpty();
        }

        @Test
        void 조회_통계가_하나도_없는_경우() {
            // given
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(DAY, 날짜_2023_11_25_토, 1);

            // when
            List<BlogVisitStatisticManageResponse> result = statisticQueryService.getBlogVisitStatistics(
                    memberId,
                    blogName,
                    cond
            );

            // then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new BlogVisitStatisticManageResponse(날짜_2023_11_25_토, 날짜_2023_11_25_토, 0)
                    ));
        }

        @Test
        void 자신의_블로그가_아닌_경우_예외() {
            // given
            Long otherMemberId = 회원을_저장한다("other");
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(DAY, 날짜_2023_11_25_토, 1);

            // when & then
            assertThatThrownBy(() -> {
                statisticQueryService.getBlogVisitStatistics(
                        otherMemberId,
                        blogName,
                        cond
                );
            }).isInstanceOf(NoAuthorityBlogException.class);
        }

        @Test
        void 일간_방문자수_통계를_구한다() {
            // given
            BlogVisitStatistic 통계_2023_11_25 = new BlogVisitStatistic(날짜_2023_11_25_토, blogName, 10);
            BlogVisitStatistic 통계_2023_11_26 = new BlogVisitStatistic(날짜_2023_11_26_일, blogName, 5);
            BlogVisitStatistic 통계_2023_11_27 = new BlogVisitStatistic(날짜_2023_11_27_월, blogName);
            BlogVisitStatistic 통계_2023_11_28 = new BlogVisitStatistic(날짜_2023_11_28_화, blogName, 100);
            blogVisitStatisticRepository.saveAll(List.of(
                    통계_2023_11_25, 통계_2023_11_26, 통계_2023_11_27, 통계_2023_11_28)
            );
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(DAY, 날짜_2023_11_28_화, 4);

            // when
            List<BlogVisitStatisticManageResponse> result = statisticQueryService.getBlogVisitStatistics(
                    memberId,
                    blogName,
                    cond
            );

            // then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new BlogVisitStatisticManageResponse(날짜_2023_11_25_토, 날짜_2023_11_25_토, 10),
                            new BlogVisitStatisticManageResponse(날짜_2023_11_26_일, 날짜_2023_11_26_일, 5),
                            new BlogVisitStatisticManageResponse(날짜_2023_11_27_월, 날짜_2023_11_27_월, 0),
                            new BlogVisitStatisticManageResponse(날짜_2023_11_28_화, 날짜_2023_11_28_화, 100)
                    ));
        }

        @Test
        void 주간_방문자수_통계를_구한다() {
            // given
            BlogVisitStatistic 통계_2023_11_15 = new BlogVisitStatistic(날짜_2023_11_15_수, blogName, 10);
            BlogVisitStatistic 통계_2023_11_20 = new BlogVisitStatistic(날짜_2023_11_20_월, blogName, 5);
            BlogVisitStatistic 통계_2023_11_21 = new BlogVisitStatistic(날짜_2023_11_21_화, blogName, 25);
            BlogVisitStatistic 통계_2023_11_26 = new BlogVisitStatistic(날짜_2023_11_26_일, blogName, 2);
            BlogVisitStatistic 통계_2023_11_27 = new BlogVisitStatistic(날짜_2023_11_27_월, blogName, 100);
            BlogVisitStatistic 통계_2023_11_28 = new BlogVisitStatistic(날짜_2023_11_28_화, blogName, 200);

            blogVisitStatisticRepository.saveAll(List.of(
                    통계_2023_11_15,
                    통계_2023_11_20,
                    통계_2023_11_21,
                    통계_2023_11_26,
                    통계_2023_11_27,
                    통계_2023_11_28
            ));
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(WEEK, 날짜_2023_11_28_화, 3);

            // when
            List<BlogVisitStatisticManageResponse> result = statisticQueryService.getBlogVisitStatistics(
                    memberId,
                    blogName,
                    cond
            );

            // then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new BlogVisitStatisticManageResponse(날짜_2023_11_13_월, 날짜_2023_11_19_일, 10),
                            new BlogVisitStatisticManageResponse(날짜_2023_11_20_월, 날짜_2023_11_26_일, 32),
                            new BlogVisitStatisticManageResponse(날짜_2023_11_27_월, 날짜_2023_12_3_일, 300)
                    ));
        }

        @Test
        void 월간_방문자수_통계를_구한다() {
            // given
            BlogVisitStatistic 통계_2023_9_1 = new BlogVisitStatistic(날짜_2023_9_1, blogName, 10);

            BlogVisitStatistic 통계_2023_10_1 = new BlogVisitStatistic(날짜_2023_10_1, blogName, 5);
            BlogVisitStatistic 통계_2023_10_20 = new BlogVisitStatistic(날짜_2023_10_20, blogName, 25);
            BlogVisitStatistic 통계_2023_10_31 = new BlogVisitStatistic(날짜_2023_10_31, blogName, 2);

            BlogVisitStatistic 통계_2023_11_1 = new BlogVisitStatistic(날짜_2023_11_1_수, blogName, 100);
            BlogVisitStatistic 통계_2023_11_30 = new BlogVisitStatistic(날짜_2023_11_30_목, blogName, 200);

            blogVisitStatisticRepository.saveAll(List.of(
                    통계_2023_9_1,
                    통계_2023_10_1,
                    통계_2023_10_20,
                    통계_2023_10_31,
                    통계_2023_11_1,
                    통계_2023_11_30
            ));
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(MONTH, 날짜_2023_11_30_목, 3);

            // when
            List<BlogVisitStatisticManageResponse> result = statisticQueryService.getBlogVisitStatistics(
                    memberId,
                    blogName,
                    cond
            );

            // then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new BlogVisitStatisticManageResponse(날짜_2023_9_1, 날짜_2023_9_30, 10),
                            new BlogVisitStatisticManageResponse(날짜_2023_10_1, 날짜_2023_10_31, 32),
                            new BlogVisitStatisticManageResponse(날짜_2023_11_1_수, 날짜_2023_11_30_목, 300)
                    ));
        }

        @Test
        void 연간_방문자수_통계를_구한다() {
            // given
            LocalDate 날짜_2022_9_3 = LocalDate.of(2022, 9, 30);
            LocalDate 날짜_2023_2_3 = LocalDate.of(2023, 11, 1);

            BlogVisitStatistic 통계_2022_1_1 = new BlogVisitStatistic(날짜_2022_1_1, blogName, 5);
            BlogVisitStatistic 통계_2022_9_3 = new BlogVisitStatistic(날짜_2022_9_3, blogName, 50);
            BlogVisitStatistic 통계_2022_12_31 = new BlogVisitStatistic(날짜_2022_12_31, blogName, 10);

            BlogVisitStatistic 통계_2023_2_3 = new BlogVisitStatistic(날짜_2023_2_3, blogName, 30);
            BlogVisitStatistic 통계_2023_11_30 = new BlogVisitStatistic(날짜_2023_11_30_목, blogName, 20);

            blogVisitStatisticRepository.saveAll(List.of(
                    통계_2022_1_1,
                    통계_2022_9_3,
                    통계_2022_12_31,
                    통계_2023_2_3,
                    통계_2023_11_30
            ));
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(YEAR, 날짜_2023_11_30_목, 4);

            // when
            List<BlogVisitStatisticManageResponse> result = statisticQueryService.getBlogVisitStatistics(memberId,
                    blogName,
                    cond
            );

            // then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new BlogVisitStatisticManageResponse(날짜_2020_1_1, 날짜_2020_12_31, 0),
                            new BlogVisitStatisticManageResponse(날짜_2021_1_1, 날짜_2021_12_31, 0),
                            new BlogVisitStatisticManageResponse(날짜_2022_1_1, 날짜_2022_12_31, 65),
                            new BlogVisitStatisticManageResponse(날짜_2023_1_1, 날짜_2023_12_31, 50)
                    ));
        }
    }

    @Nested
    class 블로그_방문자_수_통계_단순_조회_시 {

        private Long mallangId;
        private String blogName;

        @BeforeEach
        void setUp() {
            mallangId = 회원을_저장한다("주인");
            blogName = 블로그_개설(mallangId, "mallang-blog");
        }

        @Test
        void 오늘과_어제의_방문자수와_누적_방문자수를_반환한다() {
            // given
            blogVisitStatisticRepository.saveAll(List.of(
                    new BlogVisitStatistic(날짜_2020_1_1, blogName, 110),
                    new BlogVisitStatistic(날짜_2023_11_27_월, blogName, 10),
                    new BlogVisitStatistic(날짜_2023_11_28_화, blogName, 5)
            ));

            // when
            BlogVisitStatisticSimpleResponse result = statisticQueryService
                    .getSimpleBlogVisitStatistics(blogName, 날짜_2023_11_28_화);

            // then
            assertThat(result.todayVisitCount()).isEqualTo(5);
            assertThat(result.yesterdayVisitCount()).isEqualTo(10);
            assertThat(result.totalVisitCount()).isEqualTo(125);
        }

        @Test
        void 블로그가_없는_경우() {
            // given
            blogVisitStatisticRepository.saveAll(List.of(
                    new BlogVisitStatistic(날짜_2020_1_1, blogName, 110)
            ));

            // when
            BlogVisitStatisticSimpleResponse result = statisticQueryService
                    .getSimpleBlogVisitStatistics("no", 날짜_2023_11_28_화);

            // then
            assertThat(result.todayVisitCount()).isZero();
            assertThat(result.yesterdayVisitCount()).isZero();
            assertThat(result.totalVisitCount()).isZero();

        }

        @Test
        void 오늘_방문자가_없는_경우() {
            // given
            blogVisitStatisticRepository.saveAll(List.of(
                    new BlogVisitStatistic(날짜_2020_1_1, blogName, 110),
                    new BlogVisitStatistic(날짜_2023_11_27_월, blogName, 10)
            ));

            // when
            BlogVisitStatisticSimpleResponse result = statisticQueryService
                    .getSimpleBlogVisitStatistics(blogName, 날짜_2023_11_28_화);

            // then
            assertThat(result.todayVisitCount()).isZero();
            assertThat(result.yesterdayVisitCount()).isEqualTo(10);
            assertThat(result.totalVisitCount()).isEqualTo(120);
        }

        @Test
        void 어제_방문자가_없는_경우() {
            // given
            blogVisitStatisticRepository.saveAll(List.of(
                    new BlogVisitStatistic(날짜_2020_1_1, blogName, 110),
                    new BlogVisitStatistic(날짜_2023_11_28_화, blogName, 5)
            ));

            // when
            BlogVisitStatisticSimpleResponse result = statisticQueryService
                    .getSimpleBlogVisitStatistics(blogName, 날짜_2023_11_28_화);

            // then
            assertThat(result.todayVisitCount()).isEqualTo(5);
            assertThat(result.yesterdayVisitCount()).isZero();
            assertThat(result.totalVisitCount()).isEqualTo(115);
        }
    }

    @Nested
    class 포스트_누적_조회수_조회_시 {

        private Long memberId;
        private String blogName;
        private PostId postId;

        @BeforeEach
        void setUp() {
            memberId = 회원을_저장한다("말랑");
            blogName = 블로그_개설(memberId, "mallang-log");
            postId = 포스트를_저장한다(memberId, blogName, "title", "bodyText");
        }

        @Test
        void 특정_포스트의_누적_조회수를_구한다() {
            // given
            PostViewStatistic 통계_2023_11_25 = new PostViewStatistic(날짜_2023_11_25_토, postId, 10);
            PostViewStatistic 통계_2023_11_26 = new PostViewStatistic(날짜_2023_11_26_일, postId, 5);
            PostViewStatistic 통계_2023_11_27 = new PostViewStatistic(날짜_2023_11_27_월, postId);
            PostViewStatistic 통계_2023_11_28 = new PostViewStatistic(날짜_2023_11_28_화, postId, 100);
            postViewStatisticRepository.saveAll(List.of(통계_2023_11_25, 통계_2023_11_26, 통계_2023_11_27, 통계_2023_11_28));

            // when
            PostTotalViewsResponse response = statisticQueryService.getPostTotalViews(blogName, postId.getPostId());

            // then
            assertThat(response.totalViewCount()).isEqualTo(115);
        }

        @Test
        void 없는_포스트라면_예외() {
            // when & then
            assertThatThrownBy(() ->
                    statisticQueryService.getPostTotalViews(blogName, 1000L)
            ).isInstanceOf(NotFoundPostException.class);
        }
    }

    @Nested
    class 포스트_조회수_통계_조회_시 {

        private Long memberId;
        private String blogName;
        private PostId postId;

        @BeforeEach
        void setUp() {
            memberId = 회원을_저장한다("말랑");
            blogName = 블로그_개설(memberId, "mallang-log");
            postId = 포스트를_저장한다(memberId, blogName, "title", "bodyText");
        }

        @Test
        void 조회_통계가_하나도_없는_경우() {
            // given
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(DAY, 날짜_2023_11_25_토, 1);

            // when
            List<PostViewStatisticResponse> result = statisticQueryService.getPostViewStatistics(
                    memberId,
                    blogName,
                    postId.getPostId(),
                    cond
            );

            // then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new PostViewStatisticResponse(날짜_2023_11_25_토, 날짜_2023_11_25_토, 0)
                    ));
        }

        @Test
        void 자신의_포스트가_아닌_경우_예외() {
            // given
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(DAY, 날짜_2023_11_25_토, 1);
            Long otherMemberId = 회원을_저장한다("other");

            // when & then
            assertThatThrownBy(() -> {
                statisticQueryService.getPostViewStatistics(
                        otherMemberId,
                        blogName,
                        postId.getPostId(),
                        cond
                );
            }).isInstanceOf(NoAuthorityPostException.class);
        }

        @Test
        void 일간_조회수_통계를_구한다() {
            // given
            PostViewStatistic 통계_2023_11_25 = new PostViewStatistic(날짜_2023_11_25_토, postId, 10);
            PostViewStatistic 통계_2023_11_26 = new PostViewStatistic(날짜_2023_11_26_일, postId, 5);
            PostViewStatistic 통계_2023_11_27 = new PostViewStatistic(날짜_2023_11_27_월, postId);
            PostViewStatistic 통계_2023_11_28 = new PostViewStatistic(날짜_2023_11_28_화, postId, 100);
            postViewStatisticRepository.saveAll(List.of(통계_2023_11_25, 통계_2023_11_26, 통계_2023_11_27, 통계_2023_11_28));
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(DAY, 날짜_2023_11_28_화, 4);

            // when
            List<PostViewStatisticResponse> result = statisticQueryService.getPostViewStatistics(
                    memberId,
                    blogName,
                    postId.getPostId(),
                    cond
            );

            // then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new PostViewStatisticResponse(날짜_2023_11_25_토, 날짜_2023_11_25_토, 10),
                            new PostViewStatisticResponse(날짜_2023_11_26_일, 날짜_2023_11_26_일, 5),
                            new PostViewStatisticResponse(날짜_2023_11_27_월, 날짜_2023_11_27_월, 0),
                            new PostViewStatisticResponse(날짜_2023_11_28_화, 날짜_2023_11_28_화, 100)
                    ));
        }

        @Test
        void 주간_조회수_통계를_구한다() {
            // given
            PostViewStatistic 통계_2023_11_15 = new PostViewStatistic(날짜_2023_11_15_수, postId, 10);
            PostViewStatistic 통계_2023_11_20 = new PostViewStatistic(날짜_2023_11_20_월, postId, 5);
            PostViewStatistic 통계_2023_11_21 = new PostViewStatistic(날짜_2023_11_21_화, postId, 25);
            PostViewStatistic 통계_2023_11_26 = new PostViewStatistic(날짜_2023_11_26_일, postId, 2);
            PostViewStatistic 통계_2023_11_27 = new PostViewStatistic(날짜_2023_11_27_월, postId, 100);
            PostViewStatistic 통계_2023_11_28 = new PostViewStatistic(날짜_2023_11_28_화, postId, 200);
            postViewStatisticRepository.saveAll(List.of(
                    통계_2023_11_15,
                    통계_2023_11_20,
                    통계_2023_11_21,
                    통계_2023_11_26,
                    통계_2023_11_27,
                    통계_2023_11_28
            ));
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(WEEK, 날짜_2023_11_28_화, 3);

            // when
            List<PostViewStatisticResponse> result = statisticQueryService.getPostViewStatistics(
                    memberId,
                    blogName,
                    postId.getPostId(),
                    cond
            );

            // then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new PostViewStatisticResponse(날짜_2023_11_13_월, 날짜_2023_11_19_일, 10),
                            new PostViewStatisticResponse(날짜_2023_11_20_월, 날짜_2023_11_26_일, 32),
                            new PostViewStatisticResponse(날짜_2023_11_27_월, 날짜_2023_12_3_일, 300)
                    ));
        }

        @Test
        void 월간_조회수_통계를_구한다() {
            // given
            PostViewStatistic 통계_2023_9_1 = new PostViewStatistic(날짜_2023_9_1, postId, 10);

            PostViewStatistic 통계_2023_10_1 = new PostViewStatistic(날짜_2023_10_1, postId, 5);
            PostViewStatistic 통계_2023_10_20 = new PostViewStatistic(날짜_2023_10_20, postId, 25);
            PostViewStatistic 통계_2023_10_31 = new PostViewStatistic(날짜_2023_10_31, postId, 2);

            PostViewStatistic 통계_2023_11_1 = new PostViewStatistic(날짜_2023_11_1_수, postId, 100);
            PostViewStatistic 통계_2023_11_30 = new PostViewStatistic(날짜_2023_11_30_목, postId, 200);
            postViewStatisticRepository.saveAll(List.of(
                    통계_2023_9_1,
                    통계_2023_10_1,
                    통계_2023_10_20,
                    통계_2023_10_31,
                    통계_2023_11_1,
                    통계_2023_11_30
            ));
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(MONTH, 날짜_2023_11_30_목, 3);

            // when
            List<PostViewStatisticResponse> result = statisticQueryService.getPostViewStatistics(
                    memberId,
                    blogName,
                    postId.getPostId(),
                    cond
            );

            // then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new PostViewStatisticResponse(날짜_2023_9_1, 날짜_2023_9_30, 10),
                            new PostViewStatisticResponse(날짜_2023_10_1, 날짜_2023_10_31, 32),
                            new PostViewStatisticResponse(날짜_2023_11_1_수, 날짜_2023_11_30_목, 300)
                    ));
        }

        @Test
        void 연간_조회수_통계를_구한다() {
            // given
            LocalDate 날짜_2022_9_3 = LocalDate.of(2022, 9, 30);
            LocalDate 날짜_2023_2_3 = LocalDate.of(2023, 11, 1);

            PostViewStatistic 통계_2022_1_1 = new PostViewStatistic(날짜_2022_1_1, postId, 5);
            PostViewStatistic 통계_2022_9_3 = new PostViewStatistic(날짜_2022_9_3, postId, 50);
            PostViewStatistic 통계_2022_12_31 = new PostViewStatistic(날짜_2022_12_31, postId, 10);

            PostViewStatistic 통계_2023_2_3 = new PostViewStatistic(날짜_2023_2_3, postId, 30);
            PostViewStatistic 통계_2023_11_30 = new PostViewStatistic(날짜_2023_11_30_목, postId, 20);

            postViewStatisticRepository.saveAll(List.of(
                    통계_2022_1_1,
                    통계_2022_9_3,
                    통계_2022_12_31,
                    통계_2023_2_3,
                    통계_2023_11_30
            ));
            StatisticQueryCondition cond = StatisticQueryConditionConverter.convert(YEAR, 날짜_2023_11_30_목, 4);

            // when
            List<PostViewStatisticResponse> result = statisticQueryService.getPostViewStatistics(
                    memberId,
                    blogName,
                    postId.getPostId(),
                    cond
            );

            // then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(
                            new PostViewStatisticResponse(날짜_2020_1_1, 날짜_2020_12_31, 0),
                            new PostViewStatisticResponse(날짜_2021_1_1, 날짜_2021_12_31, 0),
                            new PostViewStatisticResponse(날짜_2022_1_1, 날짜_2022_12_31, 65),
                            new PostViewStatisticResponse(날짜_2023_1_1, 날짜_2023_12_31, 50)
                    ));
        }
    }
}
