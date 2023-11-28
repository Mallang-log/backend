package com.mallang.statistics.job;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.DataClearExtension;
import com.mallang.statistics.statistic.BlogVisitStatistic;
import com.mallang.statistics.statistic.BlogVisitStatisticRepository;
import com.mallang.statistics.statistic.source.BlogVisitHistory;
import com.mallang.statistics.statistic.source.BlogVisitHistoryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("블로그 방문자수 통계 작업 (BlogVisitStatisticJob) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(DataClearExtension.class)
@SpringBootTest
class BlogVisitStatisticJobTest {

    @Autowired
    private BlogVisitHistoryRepository blogVisitHistoryRepository;

    @Autowired
    private BlogVisitStatisticRepository blogVisitStatisticRepository;

    @Autowired
    private BlogVisitStatisticJob blogVisitStatisticJob;

    private final String blog1Name = "blog1name";
    private final String blog2Name = "blog2name";

    @Test
    void 집계되지_않은_모든_조회_이력을_가져와_블로그별로_그리고_일자별로_개수를_집계한다() {
        // given
        LocalDateTime 시간_2000년_10월_4일_10시_0분 = LocalDateTime.of(2000, 10, 4, 10, 0);
        LocalDateTime 시간_2000년_10월_4일_10시_59분 = LocalDateTime.of(2000, 10, 4, 10, 59);
        LocalDateTime 시간_2000년_10월_4일_11시_0분 = LocalDateTime.of(2000, 10, 4, 11, 0);
        LocalDateTime 시간_2000년_10월_5일_0시_30분 = LocalDateTime.of(2000, 10, 5, 0, 30);
        LocalDateTime 시간_2000년_10월_5일_1시_22분 = LocalDateTime.of(2000, 10, 5, 1, 22);
        LocalDateTime 시간_2001년_10월_19일_20시_2분 = LocalDateTime.of(2001, 10, 19, 20, 2);
        blogVisitHistoryRepository.save(new BlogVisitHistory(randomUUID(), blog1Name, "", "", 시간_2000년_10월_4일_10시_0분));
        blogVisitHistoryRepository.save(new BlogVisitHistory(randomUUID(), blog1Name, "", "", 시간_2000년_10월_4일_10시_59분));
        blogVisitHistoryRepository.save(new BlogVisitHistory(randomUUID(), blog1Name, "", "", 시간_2000년_10월_4일_11시_0분));

        blogVisitHistoryRepository.save(new BlogVisitHistory(randomUUID(), blog1Name, "", "", 시간_2000년_10월_5일_0시_30분));
        blogVisitHistoryRepository.save(new BlogVisitHistory(randomUUID(), blog1Name, "", "", 시간_2000년_10월_5일_0시_30분));
        blogVisitHistoryRepository.save(new BlogVisitHistory(randomUUID(), blog1Name, "", "", 시간_2000년_10월_5일_1시_22분));

        blogVisitHistoryRepository.save(new BlogVisitHistory(randomUUID(), blog1Name, "", "", 시간_2001년_10월_19일_20시_2분));

        blogVisitHistoryRepository.save(new BlogVisitHistory(randomUUID(), blog2Name, "", "", 시간_2000년_10월_4일_10시_0분));

        // when
        blogVisitStatisticJob.blogVisitsAggregationJob();

        // then
        assertThat(blogVisitHistoryRepository.findAll()).isEmpty();
        List<BlogVisitStatistic> all = blogVisitStatisticRepository.findAll();
        assertThat(all).hasSize(4);

        LocalDate 시간_2000년_10월_4일 = LocalDate.of(2000, 10, 4);
        BlogVisitStatistic 블로그_1_통계_2000년_10월_4일 = all.get(0);
        assertThat(블로그_1_통계_2000년_10월_4일.getBlogName()).isEqualTo(blog1Name);
        assertThat(블로그_1_통계_2000년_10월_4일.getStatisticDate()).isEqualTo(시간_2000년_10월_4일);
        assertThat(블로그_1_통계_2000년_10월_4일.getCount()).isEqualTo(3);

        LocalDate 시간_2000년_10월_5일 = LocalDate.of(2000, 10, 5);
        BlogVisitStatistic 블로그_1_통계_2000년_10월_5일 = all.get(1);
        assertThat(블로그_1_통계_2000년_10월_5일.getBlogName()).isEqualTo(blog1Name);
        assertThat(블로그_1_통계_2000년_10월_5일.getStatisticDate()).isEqualTo(시간_2000년_10월_5일);
        assertThat(블로그_1_통계_2000년_10월_5일.getCount()).isEqualTo(3);

        LocalDate 시간_2001년_10월_19일 = LocalDate.of(2001, 10, 19);
        BlogVisitStatistic 블로그_1_통계_2001년_10월_19일 = all.get(2);
        assertThat(블로그_1_통계_2001년_10월_19일.getBlogName()).isEqualTo(blog1Name);
        assertThat(블로그_1_통계_2001년_10월_19일.getStatisticDate()).isEqualTo(시간_2001년_10월_19일);
        assertThat(블로그_1_통계_2001년_10월_19일.getCount()).isEqualTo(1);

        BlogVisitStatistic 블로그_2_통계_2000년_10월_4일 = all.get(3);
        assertThat(블로그_2_통계_2000년_10월_4일.getBlogName()).isEqualTo(blog2Name);
        assertThat(블로그_2_통계_2000년_10월_4일.getStatisticDate()).isEqualTo(시간_2000년_10월_4일);
        assertThat(블로그_2_통계_2000년_10월_4일.getCount()).isEqualTo(1);
    }

    @Test
    void 이미_존재하는_통계에_대해서는_개수가_증가한다() {
        // given
        LocalDateTime 시간_2000년_10월_4일_10시_0분 = LocalDateTime.of(2000, 10, 4, 10, 0);
        LocalDateTime 시간_2000년_10월_4일_10시_59분 = LocalDateTime.of(2000, 10, 4, 10, 59);
        LocalDateTime 시간_2000년_10월_4일_11시_0분 = LocalDateTime.of(2000, 10, 4, 11, 0);
        blogVisitHistoryRepository.save(new BlogVisitHistory(randomUUID(), blog1Name, "", "", 시간_2000년_10월_4일_10시_0분));
        blogVisitHistoryRepository.save(new BlogVisitHistory(randomUUID(), blog1Name, "", "", 시간_2000년_10월_4일_10시_59분));
        blogVisitStatisticJob.blogVisitsAggregationJob();

        LocalDate 시간_2000년_10월_4일 = LocalDate.of(2000, 10, 4);
        Optional<BlogVisitStatistic> statistic = blogVisitStatisticRepository
                .findByBlogNameAndStatisticDate(blog1Name, 시간_2000년_10월_4일);
        assertThat(statistic).isPresent();
        BlogVisitStatistic postViewStatistic = statistic.get();
        assertThat(postViewStatistic.getBlogName()).isEqualTo(blog1Name);
        assertThat(postViewStatistic.getStatisticDate()).isEqualTo(시간_2000년_10월_4일);
        assertThat(postViewStatistic.getCount()).isEqualTo(2);

        // when
        blogVisitHistoryRepository.save(new BlogVisitHistory(randomUUID(), blog1Name, "", "", 시간_2000년_10월_4일_11시_0분));
        blogVisitStatisticJob.blogVisitsAggregationJob();

        // then
        assertThat(blogVisitHistoryRepository.findAll()).isEmpty();

        statistic = blogVisitStatisticRepository.findByBlogNameAndStatisticDate(blog1Name, 시간_2000년_10월_4일);
        assertThat(statistic).isPresent();
        postViewStatistic = statistic.get();
        assertThat(postViewStatistic.getBlogName()).isEqualTo(blog1Name);
        assertThat(postViewStatistic.getStatisticDate()).isEqualTo(시간_2000년_10월_4일);
        assertThat(postViewStatistic.getCount()).isEqualTo(3);
    }
}
