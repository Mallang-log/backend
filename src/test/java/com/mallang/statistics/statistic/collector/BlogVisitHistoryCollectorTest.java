package com.mallang.statistics.statistic.collector;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mallang.statistics.statistic.source.BlogVisitHistory;
import com.mallang.statistics.statistic.source.BlogVisitHistoryRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("블로그 방문 이력 수집기 (BlogVisitHistoryCollector) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogVisitHistoryCollectorTest {

    private final BlogVisitHistoryRepository blogVisitHistoryRepository = mock(BlogVisitHistoryRepository.class);
    private final BlogVisitHistoryCollector blogVisitHistoryCollector =
            new BlogVisitHistoryCollector(blogVisitHistoryRepository);

    @Test
    void 기존_블로그_방문_이력이_없으면_곧바로_저장한다() {
        // given
        UUID uuid = UUID.randomUUID();
        BlogVisitHistory blogVisitHistory = BlogVisitHistory.builder()
                .uuid(uuid)
                .blogName("mallang-log")
                .createdDate(LocalDateTime.of(2022, 10, 4, 14, 20)) // 2022.10.4 - 14:20
                .ip("127.0.0.1")
                .build();
        given(blogVisitHistoryRepository
                .findFirstByUuidAndBlogNameOrderByCreatedDateDesc(uuid, "mallang-log"))
                .willReturn(Optional.empty());

        // when
        blogVisitHistoryCollector.save(blogVisitHistory);

        // then
        verify(blogVisitHistoryRepository, times(1)).save(blogVisitHistory);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2022-10-03T14:20",  // 하루 차이
            "2022-10-03T14:19",  // 하루하고 1분 차이
    })
    void 하루_이내에_포스트를_조회한_이력이_없다면_저장한다(String time) {
        // given
        String now = "2022-10-04T14:20";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);

        UUID uuid = UUID.randomUUID();
        BlogVisitHistory blogVisitHistory = BlogVisitHistory.builder()
                .uuid(uuid)
                .blogName("mallang-log")
                .createdDate(LocalDateTime.parse(now, formatter))
                .ip("127.0.0.1")
                .build();
        BlogVisitHistory last = BlogVisitHistory.builder()
                .uuid(uuid)
                .blogName("mallang-log")
                .createdDate(localDateTime)
                .ip("123.456.789.10")
                .build();
        given(blogVisitHistoryRepository
                .findFirstByUuidAndBlogNameOrderByCreatedDateDesc(uuid, "mallang-log"))
                .willReturn(Optional.of(last));

        // when
        blogVisitHistoryCollector.save(blogVisitHistory);

        // then
        verify(blogVisitHistoryRepository, times(1)).save(blogVisitHistory);
    }

    @Test
    void 가장_마지막으로_저장된_이력이_하루_이내라면_저장하지_않는다() {
        // given
        String now = "2022-10-04T14:20";
        String lastTime = "2022-10-03T14:21";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(lastTime, formatter);
        UUID uuid = UUID.randomUUID();
        BlogVisitHistory blogVisitHistory = BlogVisitHistory.builder()
                .uuid(uuid)
                .blogName("mallang-log")
                .createdDate(LocalDateTime.parse(now, formatter))
                .ip("127.0.0.1")
                .build();
        BlogVisitHistory last = BlogVisitHistory.builder()
                .uuid(uuid)
                .blogName("mallang-log")
                .createdDate(localDateTime)
                .ip("123.456.789.10")
                .build();
        given(blogVisitHistoryRepository
                .findFirstByUuidAndBlogNameOrderByCreatedDateDesc(uuid, "mallang-log"))
                .willReturn(Optional.of(last));

        // when
        blogVisitHistoryCollector.save(blogVisitHistory);

        // then
        verify(blogVisitHistoryRepository, times(0)).save(blogVisitHistory);
    }
}
