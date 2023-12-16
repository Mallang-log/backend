package com.mallang.statistics.statistic.application;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.post.domain.PostDeleteEvent;
import com.mallang.post.domain.PostId;
import com.mallang.statistics.statistic.PostViewStatisticRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 조회 통계 이벤트 핸들러 (PostViewStatisticEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostViewStatisticEventHandlerTest {

    private final PostViewStatisticRepository postViewStatisticRepository = mock(PostViewStatisticRepository.class);
    private final PostViewStatisticEventHandler postViewStatisticEventHandler = new PostViewStatisticEventHandler(
            postViewStatisticRepository
    );

    @Test
    void 포스트_제거_이벤트를_받아_해당_포스트의_조회_이력을_제거한다() {
        // given
        PostId postId = new PostId(1L, 1L);
        PostDeleteEvent postDeleteEvent = new PostDeleteEvent(postId);

        // when
        postViewStatisticEventHandler.deletePostViewStatistic(postDeleteEvent);

        // then
        then(postViewStatisticRepository)
                .should(times(1))
                .deleteAllByPostId(postId);
    }
}
