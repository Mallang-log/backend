package com.mallang.statistics.batch;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.DataClearExtension;
import com.mallang.post.domain.PostId;
import com.mallang.statistics.statistic.PostViewStatistic;
import com.mallang.statistics.statistic.PostViewStatisticRepository;
import com.mallang.statistics.statistic.source.PostViewHistory;
import com.mallang.statistics.statistic.source.PostViewHistoryRepository;
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

@DisplayName("포스트 조회수 통계 작업 (PostViewStatisticJobLegacy) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(DataClearExtension.class)
@SpringBootTest
class PostViewStatisticJobLegacyTest {

    @Autowired
    private PostViewHistoryRepository postViewHistoryRepository;

    @Autowired
    private PostViewStatisticRepository postViewStatisticRepository;

    @Autowired
    private PostViewStatisticJobLegacy postViewStatisticJobLegacy;

    private final PostId postId1 = new PostId(1L, 1L);
    private final PostId postId2 = new PostId(2L, 1L);

    @Test
    void 특정_시간대에_포함되지_않으면_집계하지_않는다() {
        // given
        LocalDateTime 시간_2000년_10월_4일_10시_0분 = LocalDateTime.of(2000, 10, 4, 10, 0);
        LocalDateTime 시간_2000년_10월_4일_10시_59분 = LocalDateTime.of(2000, 10, 4, 10, 59);
        LocalDateTime 시간_2000년_10월_4일_11시_0분 = LocalDateTime.of(2000, 10, 4, 11, 0);
        postViewHistoryRepository.save(new PostViewHistory(
                randomUUID(),
                postId1,
                시간_2000년_10월_4일_10시_0분
        ));
        postViewHistoryRepository.save(new PostViewHistory(
                randomUUID(),
                postId1,
                시간_2000년_10월_4일_10시_59분
        ));
        PostViewHistory exclude = postViewHistoryRepository.save(new PostViewHistory(
                randomUUID(),
                postId1,
                시간_2000년_10월_4일_11시_0분
        ));

        // when
        postViewStatisticJobLegacy.postViewsAggregationJob(시간_2000년_10월_4일_10시_0분, 시간_2000년_10월_4일_11시_0분);

        // then
        assertThat(postViewHistoryRepository.findAll().get(0).getId())
                .isEqualTo(exclude.getId());
        List<PostViewStatistic> all = postViewStatisticRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getCount()).isEqualTo(2);
    }

    @Test
    void 시작시간은_포함되며_끝시간은_포함되지_않는다() {
        // given
        LocalDateTime 시간_2000년_10월_4일_10시_0분 = LocalDateTime.of(2000, 10, 4, 10, 0);
        LocalDateTime 시간_2000년_10월_4일_11시_0분 = LocalDateTime.of(2000, 10, 4, 11, 0);
        LocalDateTime 시간_2000년_10월_4일_12시_0분 = LocalDateTime.of(2000, 10, 4, 12, 0);
        PostViewHistory 시간_2000년_10월_4일_10시_0분_이력 = postViewHistoryRepository.save(
                new PostViewHistory(
                        randomUUID(),
                        postId1,
                        시간_2000년_10월_4일_10시_0분
                ));
        PostViewHistory 시간_2000년_10월_4일_11시_0분_이력 = postViewHistoryRepository.save(
                new PostViewHistory(
                        randomUUID(),
                        postId1,
                        시간_2000년_10월_4일_11시_0분
                ));

        // when
        postViewStatisticJobLegacy.postViewsAggregationJob(시간_2000년_10월_4일_10시_0분, 시간_2000년_10월_4일_11시_0분);

        // then
        assertThat(postViewHistoryRepository.findById(시간_2000년_10월_4일_11시_0분_이력.getId())).isPresent();
        assertThat(postViewHistoryRepository.findById(시간_2000년_10월_4일_10시_0분_이력.getId())).isEmpty();
        assertThat(postViewStatisticRepository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(PostViewStatistic::getCount)
                .isEqualTo(1);

        // when
        postViewStatisticJobLegacy.postViewsAggregationJob(시간_2000년_10월_4일_11시_0분, 시간_2000년_10월_4일_12시_0분);

        // then
        assertThat(postViewHistoryRepository.findById(시간_2000년_10월_4일_11시_0분_이력.getId())).isEmpty();
        assertThat(postViewStatisticRepository.findAll())
                .hasSize(1)
                .element(0)
                .extracting(PostViewStatistic::getCount)
                .isEqualTo(2);
    }

    @Test
    void 특정_시간대의_집계되지_않은_모든_조회_이력을_가져와_블로그별로_그리고_일자별로_개수를_집계한다() {
        // given
        LocalDateTime 시간_2000년_10월_4일_10시_0분 = LocalDateTime.of(2000, 10, 4, 10, 0);
        LocalDateTime 시간_2000년_10월_4일_10시_59분 = LocalDateTime.of(2000, 10, 4, 10, 59);
        LocalDateTime 시간_2000년_10월_4일_11시_0분 = LocalDateTime.of(2000, 10, 4, 11, 0);
        LocalDateTime 시간_2000년_10월_5일_0시_30분 = LocalDateTime.of(2000, 10, 5, 0, 30);
        LocalDateTime 시간_2000년_10월_5일_1시_22분 = LocalDateTime.of(2000, 10, 5, 1, 22);
        LocalDateTime 시간_2001년_10월_19일_20시_2분 = LocalDateTime.of(2001, 10, 19, 20, 2);
        LocalDateTime 시간_2001년_10월_19일_20시_2분_0초_1나노초 = LocalDateTime.of(2001, 10, 19, 20, 2, 0, 1);
        postViewHistoryRepository.save(new PostViewHistory(randomUUID(), postId1, 시간_2000년_10월_4일_10시_0분));
        postViewHistoryRepository.save(new PostViewHistory(randomUUID(), postId1, 시간_2000년_10월_4일_10시_59분));
        postViewHistoryRepository.save(new PostViewHistory(randomUUID(), postId1, 시간_2000년_10월_4일_11시_0분));

        postViewHistoryRepository.save(new PostViewHistory(randomUUID(), postId1, 시간_2000년_10월_5일_0시_30분));
        postViewHistoryRepository.save(new PostViewHistory(randomUUID(), postId1, 시간_2000년_10월_5일_0시_30분));
        postViewHistoryRepository.save(new PostViewHistory(randomUUID(), postId1, 시간_2000년_10월_5일_1시_22분));

        postViewHistoryRepository.save(new PostViewHistory(randomUUID(), postId1, 시간_2001년_10월_19일_20시_2분));
        postViewHistoryRepository.save(new PostViewHistory(randomUUID(), postId2, 시간_2000년_10월_4일_10시_0분));

        // when
        postViewStatisticJobLegacy.postViewsAggregationJob(시간_2000년_10월_4일_10시_0분, 시간_2001년_10월_19일_20시_2분_0초_1나노초);

        // then
        assertThat(postViewHistoryRepository.findAll()).isEmpty();
        List<PostViewStatistic> all = postViewStatisticRepository.findAll();
        assertThat(all).hasSize(4);

        LocalDate 시간_2000년_10월_4일 = LocalDate.of(2000, 10, 4);
        PostViewStatistic 포스트_2_통계_2000년_10월_4일 = all.get(0);
        assertThat(포스트_2_통계_2000년_10월_4일.getPostId().getPostId()).isEqualTo(2);
        assertThat(포스트_2_통계_2000년_10월_4일.getStatisticDate()).isEqualTo(시간_2000년_10월_4일);
        assertThat(포스트_2_통계_2000년_10월_4일.getCount()).isEqualTo(1);

        PostViewStatistic 포스트_1_통계_2000년_10월_4일 = all.get(1);
        assertThat(포스트_1_통계_2000년_10월_4일.getPostId().getPostId()).isEqualTo(1);
        assertThat(포스트_1_통계_2000년_10월_4일.getStatisticDate()).isEqualTo(시간_2000년_10월_4일);
        assertThat(포스트_1_통계_2000년_10월_4일.getCount()).isEqualTo(3);

        LocalDate 시간_2000년_10월_5일 = LocalDate.of(2000, 10, 5);
        PostViewStatistic 포스트_1_통계_2000년_10월_5일 = all.get(2);
        assertThat(포스트_1_통계_2000년_10월_5일.getPostId().getPostId()).isEqualTo(1);
        assertThat(포스트_1_통계_2000년_10월_5일.getStatisticDate()).isEqualTo(시간_2000년_10월_5일);
        assertThat(포스트_1_통계_2000년_10월_5일.getCount()).isEqualTo(3);

        LocalDate 시간_2001년_10월_19일 = LocalDate.of(2001, 10, 19);
        PostViewStatistic 포스트_1_통계_2001년_10월_19일 = all.get(3);
        assertThat(포스트_1_통계_2001년_10월_19일.getPostId().getPostId()).isEqualTo(1);
        assertThat(포스트_1_통계_2001년_10월_19일.getStatisticDate()).isEqualTo(시간_2001년_10월_19일);
        assertThat(포스트_1_통계_2001년_10월_19일.getCount()).isEqualTo(1);
    }

    @Test
    void 이미_존재하는_통계에_대해서는_개수가_증가한다() {
        // given
        LocalDateTime 시간_2000년_10월_4일_10시_0분 = LocalDateTime.of(2000, 10, 4, 10, 0);
        LocalDateTime 시간_2000년_10월_4일_10시_59분 = LocalDateTime.of(2000, 10, 4, 10, 59);
        LocalDateTime 시간_2000년_10월_4일_11시_0분 = LocalDateTime.of(2000, 10, 4, 11, 0);
        LocalDateTime 시간_2000년_10월_4일_12시 = LocalDateTime.of(2000, 10, 4, 12, 0);
        postViewHistoryRepository.save(new PostViewHistory(randomUUID(), postId1, 시간_2000년_10월_4일_10시_0분));
        postViewHistoryRepository.save(new PostViewHistory(randomUUID(), postId1, 시간_2000년_10월_4일_10시_59분));
        postViewStatisticJobLegacy.postViewsAggregationJob(시간_2000년_10월_4일_10시_0분, 시간_2000년_10월_4일_12시);
        LocalDate 시간_2000년_10월_4일 = LocalDate.of(2000, 10, 4);
        Optional<PostViewStatistic> statistic = postViewStatisticRepository
                .findByPostIdAndStatisticDate(postId1, 시간_2000년_10월_4일);
        assertThat(statistic).isPresent();
        PostViewStatistic postViewStatistic = statistic.get();
        assertThat(postViewStatistic.getPostId().getPostId()).isEqualTo(1);
        assertThat(postViewStatistic.getStatisticDate()).isEqualTo(시간_2000년_10월_4일);
        assertThat(postViewStatistic.getCount()).isEqualTo(2);

        // when
        postViewHistoryRepository.save(new PostViewHistory(randomUUID(), postId1, 시간_2000년_10월_4일_11시_0분));
        postViewStatisticJobLegacy.postViewsAggregationJob(시간_2000년_10월_4일_10시_0분, 시간_2000년_10월_4일_12시);

        // then
        assertThat(postViewHistoryRepository.findAll()).isEmpty();

        statistic = postViewStatisticRepository.findByPostIdAndStatisticDate(postId1, 시간_2000년_10월_4일);
        assertThat(statistic).isPresent();
        postViewStatistic = statistic.get();
        assertThat(postViewStatistic.getPostId().getPostId()).isEqualTo(1);
        assertThat(postViewStatistic.getStatisticDate()).isEqualTo(시간_2000년_10월_4일);
        assertThat(postViewStatistic.getCount()).isEqualTo(3);
    }
}
