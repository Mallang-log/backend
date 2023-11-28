package com.mallang.statistics.api.query.dao;

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

import com.mallang.blog.exception.NotFoundBlogException;
import com.mallang.common.ServiceTest;
import com.mallang.statistics.api.query.StatisticCondition;
import com.mallang.statistics.api.query.response.BlogVisitStatisticManageResponse;
import com.mallang.statistics.statistic.BlogVisitStatistic;
import com.mallang.statistics.statistic.BlogVisitStatisticRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("블로그 방문 통계 관리 DAO(BlogVisitStatisticManageDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogVisitStatisticManageDaoTest extends ServiceTest {

    private Long memberId;
    private String blogName;

    @Autowired
    private BlogVisitStatisticRepository blogVisitStatisticRepository;

    @Autowired
    private BlogVisitStatisticManageDao blogVisitStatisticManageDao;

    @BeforeEach
    void setUp() {
        memberId = 회원을_저장한다("말랑");
        blogName = 블로그_개설(memberId, "mallang-log");
    }

    @Test
    void 날짜가_잘못_들어온_경우() {
        StatisticCondition cond = new StatisticCondition(DAY, 날짜_2023_11_26_일, 날짜_2023_11_25_토);

        // when
        List<BlogVisitStatisticManageResponse> result = blogVisitStatisticManageDao.find(memberId, blogName, cond);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 조회_통계가_하나도_없는_경우() {
        // given
        StatisticCondition cond = new StatisticCondition(DAY, 날짜_2023_11_25_토, 날짜_2023_11_25_토);

        // when
        List<BlogVisitStatisticManageResponse> result = blogVisitStatisticManageDao.find(memberId, blogName, cond);

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
        StatisticCondition cond = new StatisticCondition(DAY, 날짜_2023_11_25_토, 날짜_2023_11_25_토);

        // when & then
        assertThatThrownBy(() -> {
            blogVisitStatisticManageDao.find(memberId + 1L, blogName, cond);
        }).isInstanceOf(NotFoundBlogException.class);
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
        StatisticCondition cond = new StatisticCondition(DAY, 날짜_2023_11_25_토, 날짜_2023_11_28_화);

        // when
        List<BlogVisitStatisticManageResponse> result = blogVisitStatisticManageDao.find(memberId, blogName, cond);

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
        StatisticCondition cond = new StatisticCondition(WEEK, 날짜_2023_11_13_월, 날짜_2023_11_28_화);

        // when
        List<BlogVisitStatisticManageResponse> result = blogVisitStatisticManageDao.find(memberId, blogName, cond);

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
        StatisticCondition cond = new StatisticCondition(MONTH, 날짜_2023_9_1, 날짜_2023_11_30_목);

        // when
        List<BlogVisitStatisticManageResponse> result = blogVisitStatisticManageDao.find(memberId, blogName, cond);

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
        StatisticCondition cond = new StatisticCondition(YEAR, 날짜_2020_1_1, 날짜_2023_11_30_목);

        // when
        List<BlogVisitStatisticManageResponse> result = blogVisitStatisticManageDao.find(memberId, blogName, cond);

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
