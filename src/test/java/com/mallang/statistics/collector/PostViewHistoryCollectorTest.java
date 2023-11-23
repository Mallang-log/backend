package com.mallang.statistics.collector;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mallang.statistics.history.PostViewHistory;
import com.mallang.statistics.history.PostViewHistoryRepository;
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

@DisplayName("포스트 조회 이력 수집기(PostViewHistoryCollector) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostViewHistoryCollectorTest {

    private final PostViewHistoryRepository postViewHistoryRepository = mock(PostViewHistoryRepository.class);
    private final PostViewHistoryCollector postViewHistoryCollector =
            new PostViewHistoryCollector(postViewHistoryRepository);

    @Test
    void 기존_포스트_조회_이력이_없으면_곧바로_저장한다() {
        // given
        UUID uuid = UUID.randomUUID();
        PostViewHistory postViewHistory = new PostViewHistory(uuid, 1L,
                LocalDateTime.of(2022, 10, 4, 14, 20) // 2022.10.4 - 14:20
        );
        given(postViewHistoryRepository.findFirstByUuidAndPostIdOrderByCreatedDateDesc(uuid, 1L))
                .willReturn(Optional.empty());

        // when
        postViewHistoryCollector.save(postViewHistory);

        // then
        verify(postViewHistoryRepository, times(1)).save(postViewHistory);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2022-10-04T13:20",  // 한시간 차이
            "2022-10-04T13:19",  // 한시간 1분 차이
    })
    void 한시간_이내에_포스트를_조회한_이력이_없다면_저장한다(String time) {
        // given
        String now = "2022-10-04T14:20";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);

        UUID uuid = UUID.randomUUID();
        PostViewHistory postViewHistory = new PostViewHistory(uuid, 1L, LocalDateTime.parse(now, formatter));
        PostViewHistory last = new PostViewHistory(uuid, 1L, localDateTime);
        given(postViewHistoryRepository.findFirstByUuidAndPostIdOrderByCreatedDateDesc(uuid, 1L))
                .willReturn(Optional.of(last));

        // when
        postViewHistoryCollector.save(postViewHistory);

        // then
        verify(postViewHistoryRepository, times(1)).save(postViewHistory);
    }

    @Test
    void 가장_마지막으로_저장된_이력이_한시간_이내라면_저장하지_않는다() {
        // given
        String now = "2022-10-04T14:20";
        String lastTime = "2022-10-04T13:21";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(lastTime, formatter);

        UUID uuid = UUID.randomUUID();
        PostViewHistory postViewHistory = new PostViewHistory(uuid, 1L, LocalDateTime.parse(now, formatter));
        PostViewHistory last = new PostViewHistory(uuid, 1L, localDateTime);
        given(postViewHistoryRepository.findFirstByUuidAndPostIdOrderByCreatedDateDesc(uuid, 1L))
                .willReturn(Optional.of(last));

        // when
        postViewHistoryCollector.save(postViewHistory);

        // then
        verify(postViewHistoryRepository, times(0)).save(any());
    }
}
