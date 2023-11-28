package com.mallang.statistics.query.dao;

import static com.mallang.statistics.query.support.PeriodType.DAY;
import static com.mallang.statistics.query.support.PeriodType.MONTH;
import static com.mallang.statistics.query.support.PeriodType.WEEK;
import static com.mallang.statistics.query.support.PeriodType.YEAR;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.ServiceTest;
import com.mallang.post.domain.PostId;
import com.mallang.statistics.query.StatisticCondition;
import com.mallang.statistics.query.response.PostViewStatisticResponse;
import com.mallang.statistics.statistic.PostViewStatistic;
import com.mallang.statistics.statistic.PostViewStatisticRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("포스트 조회수 통계 DAO(PostViewStatisticDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostViewStatisticDaoTest extends ServiceTest {

    private Long memberId;
    private String blogName;
    private PostId postId;

    @Autowired
    private PostViewStatisticRepository postViewStatisticRepository;

    @Autowired
    private PostViewStatisticDao postViewStatisticDao;

    @BeforeEach
    void setUp() {
        memberId = 회원을_저장한다("말랑");
        blogName = 블로그_개설(memberId, "mallang-log");
        postId = 포스트를_저장한다(memberId, blogName, "title", "content");
    }

    @Test
    void 일간_조회수_통계를_구한다() {
        // given
        LocalDate 날짜_2023_11_25 = LocalDate.of(2023, 11, 25);
        LocalDate 날짜_2023_11_26 = LocalDate.of(2023, 11, 26);
        LocalDate 날짜_2023_11_27 = LocalDate.of(2023, 11, 27);
        LocalDate 날짜_2023_11_28 = LocalDate.of(2023, 11, 28);
        PostViewStatistic 통계_2023_11_25 = new PostViewStatistic(날짜_2023_11_25, postId);
        통계_2023_11_25.addCount(10);
        PostViewStatistic 통계_2023_11_26 = new PostViewStatistic(날짜_2023_11_26, postId);
        통계_2023_11_26.addCount(5);
        PostViewStatistic 통계_2023_11_27 = new PostViewStatistic(날짜_2023_11_27, postId);
        PostViewStatistic 통계_2023_11_28 = new PostViewStatistic(날짜_2023_11_28, postId);
        통계_2023_11_28.addCount(100);
        postViewStatisticRepository.saveAll(List.of(통계_2023_11_25, 통계_2023_11_26, 통계_2023_11_27, 통계_2023_11_28));
        StatisticCondition cond = new StatisticCondition(DAY, 날짜_2023_11_25, 날짜_2023_11_28);

        // when
        List<PostViewStatisticResponse> result = postViewStatisticDao.find(memberId, blogName, postId.getId(), cond);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new PostViewStatisticResponse(날짜_2023_11_25, 날짜_2023_11_25, 10),
                        new PostViewStatisticResponse(날짜_2023_11_26, 날짜_2023_11_26, 5),
                        new PostViewStatisticResponse(날짜_2023_11_27, 날짜_2023_11_27, 0),
                        new PostViewStatisticResponse(날짜_2023_11_28, 날짜_2023_11_28, 100)
                ));
    }

    @Test
    void 주간_조회수_통계를_구한다() {
        // given
        LocalDate 날짜_2023_11_13 = LocalDate.of(2023, 11, 13);  // 저저번주 시작
        LocalDate 날짜_2023_11_15 = LocalDate.of(2023, 11, 15);  // 저저번주
        LocalDate 날짜_2023_11_19 = LocalDate.of(2023, 11, 19);  // 저저번주 끝

        LocalDate 날짜_2023_11_20 = LocalDate.of(2023, 11, 20);  // 저번주 시작
        LocalDate 날짜_2023_11_21 = LocalDate.of(2023, 11, 21);  // 저번주
        LocalDate 날짜_2023_11_26 = LocalDate.of(2023, 11, 26);  // 저번주 끝

        LocalDate 날짜_2023_11_27 = LocalDate.of(2023, 11, 27);  // 이번주 시작
        LocalDate 날짜_2023_11_28 = LocalDate.of(2023, 11, 28);  // 이번주
        LocalDate 날짜_2023_12_3 = LocalDate.of(2023, 12, 3);  // 이번주 끝

        PostViewStatistic 통계_2023_11_15 = new PostViewStatistic(날짜_2023_11_15, postId);
        통계_2023_11_15.addCount(10);

        PostViewStatistic 통계_2023_11_20 = new PostViewStatistic(날짜_2023_11_20, postId);
        통계_2023_11_20.addCount(5);
        PostViewStatistic 통계_2023_11_21 = new PostViewStatistic(날짜_2023_11_21, postId);
        통계_2023_11_21.addCount(25);
        PostViewStatistic 통계_2023_11_26 = new PostViewStatistic(날짜_2023_11_26, postId);
        통계_2023_11_26.addCount(2);

        PostViewStatistic 통계_2023_11_27 = new PostViewStatistic(날짜_2023_11_27, postId);
        통계_2023_11_27.addCount(100);
        PostViewStatistic 통계_2023_11_28 = new PostViewStatistic(날짜_2023_11_28, postId);
        통계_2023_11_28.addCount(200);

        postViewStatisticRepository.saveAll(List.of(
                통계_2023_11_15,
                통계_2023_11_20,
                통계_2023_11_21,
                통계_2023_11_26,
                통계_2023_11_27,
                통계_2023_11_28
        ));
        StatisticCondition cond = new StatisticCondition(WEEK, 날짜_2023_11_13, 날짜_2023_11_28);

        // when
        List<PostViewStatisticResponse> result = postViewStatisticDao.find(memberId, blogName, postId.getId(), cond);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new PostViewStatisticResponse(날짜_2023_11_13, 날짜_2023_11_19, 10),
                        new PostViewStatisticResponse(날짜_2023_11_20, 날짜_2023_11_26, 32),
                        new PostViewStatisticResponse(날짜_2023_11_27, 날짜_2023_12_3, 300)
                ));
    }

    @Test
    void 월간_조회수_통계를_구한다() {
        // given
        LocalDate 날짜_2023_9_1 = LocalDate.of(2023, 9, 1);
        LocalDate 날짜_2023_9_30 = LocalDate.of(2023, 9, 30);

        LocalDate 날짜_2023_10_1 = LocalDate.of(2023, 10, 1);
        LocalDate 날짜_2023_10_20 = LocalDate.of(2023, 10, 20);
        LocalDate 날짜_2023_10_31 = LocalDate.of(2023, 10, 31);

        LocalDate 날짜_2023_11_1 = LocalDate.of(2023, 11, 1);
        LocalDate 날짜_2023_11_30 = LocalDate.of(2023, 11, 30);

        PostViewStatistic 통계_2023_9_1 = new PostViewStatistic(날짜_2023_9_1, postId);
        통계_2023_9_1.addCount(10);

        PostViewStatistic 통계_2023_10_1 = new PostViewStatistic(날짜_2023_10_1, postId);
        통계_2023_10_1.addCount(5);
        PostViewStatistic 통계_2023_10_20 = new PostViewStatistic(날짜_2023_10_20, postId);
        통계_2023_10_20.addCount(25);
        PostViewStatistic 통계_2023_10_31 = new PostViewStatistic(날짜_2023_10_31, postId);
        통계_2023_10_31.addCount(2);

        PostViewStatistic 통계_2023_11_1 = new PostViewStatistic(날짜_2023_11_1, postId);
        통계_2023_11_1.addCount(100);
        PostViewStatistic 통계_2023_11_30 = new PostViewStatistic(날짜_2023_11_30, postId);
        통계_2023_11_30.addCount(200);

        postViewStatisticRepository.saveAll(List.of(
                통계_2023_9_1,
                통계_2023_10_1,
                통계_2023_10_20,
                통계_2023_10_31,
                통계_2023_11_1,
                통계_2023_11_30
        ));
        StatisticCondition cond = new StatisticCondition(MONTH, 날짜_2023_9_1, 날짜_2023_11_30);

        // when
        List<PostViewStatisticResponse> result = postViewStatisticDao.find(memberId, blogName, postId.getId(), cond);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new PostViewStatisticResponse(날짜_2023_9_1, 날짜_2023_9_30, 10),
                        new PostViewStatisticResponse(날짜_2023_10_1, 날짜_2023_10_31, 32),
                        new PostViewStatisticResponse(날짜_2023_11_1, 날짜_2023_11_30, 300)
                ));
    }

    @Test
    void 연간_조회수_통계를_구한다() {
        // given
        LocalDate 날짜_2020_1_1 = LocalDate.of(2020, 1, 1);
        LocalDate 날짜_2020_12_31 = LocalDate.of(2020, 12, 31);

        LocalDate 날짜_2021_1_1 = LocalDate.of(2021, 1, 1);
        LocalDate 날짜_2021_12_31 = LocalDate.of(2021, 12, 31);

        LocalDate 날짜_2022_1_1 = LocalDate.of(2022, 1, 1);
        LocalDate 날짜_2022_9_3 = LocalDate.of(2022, 9, 30);
        LocalDate 날짜_2022_12_31 = LocalDate.of(2022, 12, 31);

        LocalDate 날짜_2023_1_1 = LocalDate.of(2023, 1, 1);
        LocalDate 날짜_2023_2_3 = LocalDate.of(2023, 11, 1);
        LocalDate 날짜_2023_11_30 = LocalDate.of(2023, 11, 30);
        LocalDate 날짜_2023_12_31 = LocalDate.of(2023, 12, 31);

        PostViewStatistic 통계_2022_1_1 = new PostViewStatistic(날짜_2022_1_1, postId);
        통계_2022_1_1.addCount(5);
        PostViewStatistic 통계_2022_9_3 = new PostViewStatistic(날짜_2022_9_3, postId);
        통계_2022_9_3.addCount(50);
        PostViewStatistic 통계_2022_12_31 = new PostViewStatistic(날짜_2022_12_31, postId);
        통계_2022_12_31.addCount(10);

        PostViewStatistic 통계_2023_2_3 = new PostViewStatistic(날짜_2023_2_3, postId);
        통계_2023_2_3.addCount(30);
        PostViewStatistic 통계_2023_11_30 = new PostViewStatistic(날짜_2023_11_30, postId);
        통계_2023_11_30.addCount(20);

        postViewStatisticRepository.saveAll(List.of(
                통계_2022_1_1,
                통계_2022_9_3,
                통계_2022_12_31,
                통계_2023_2_3,
                통계_2023_11_30
        ));
        StatisticCondition cond = new StatisticCondition(YEAR, 날짜_2020_1_1, 날짜_2023_11_30);

        // when
        List<PostViewStatisticResponse> result = postViewStatisticDao.find(memberId, blogName, postId.getId(), cond);

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
