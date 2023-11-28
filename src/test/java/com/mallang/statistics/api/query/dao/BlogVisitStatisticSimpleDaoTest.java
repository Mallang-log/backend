package com.mallang.statistics.api.query.dao;

import static com.mallang.common.LocalDateFixture.날짜_2020_1_1;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_27_월;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_28_화;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.ServiceTest;
import com.mallang.statistics.api.query.response.BlogVisitStatisticSimpleResponse;
import com.mallang.statistics.statistic.BlogVisitStatistic;
import com.mallang.statistics.statistic.BlogVisitStatisticRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("블로그 방문자 수 통계 단순 조회 DAO (BlogVisitStatisticSimpleDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogVisitStatisticSimpleDaoTest extends ServiceTest {

    @Autowired
    private BlogVisitStatisticSimpleDao blogVisitStatisticSimpleDao;

    @Autowired
    private BlogVisitStatisticRepository blogVisitStatisticRepository;

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
        BlogVisitStatisticSimpleResponse result = blogVisitStatisticSimpleDao
                .find(blogName, 날짜_2023_11_28_화);

        // then
        assertThat(result.todayVisitCount()).isEqualTo(5);
        assertThat(result.yesterdayVisitCount()).isEqualTo(10);
        assertThat(result.totalVisitCount()).isEqualTo(125);
    }

    @Test
    void 블로그가_없는_경우() {
        // given
        blogVisitStatisticRepository.saveAll(List.of(
                new BlogVisitStatistic(날짜_2020_1_1, blogName, 110),
                new BlogVisitStatistic(날짜_2023_11_27_월, blogName, 10),
                new BlogVisitStatistic(날짜_2023_11_28_화, blogName, 5)
        ));

        // when
        BlogVisitStatisticSimpleResponse result = blogVisitStatisticSimpleDao
                .find("no", 날짜_2023_11_28_화);

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
        BlogVisitStatisticSimpleResponse result = blogVisitStatisticSimpleDao
                .find(blogName, 날짜_2023_11_28_화);

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
        BlogVisitStatisticSimpleResponse result = blogVisitStatisticSimpleDao
                .find(blogName, 날짜_2023_11_28_화);

        // then
        assertThat(result.todayVisitCount()).isEqualTo(5);
        assertThat(result.yesterdayVisitCount()).isZero();
        assertThat(result.totalVisitCount()).isEqualTo(115);
    }
}
