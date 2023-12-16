package com.mallang.statistics.statistic.application;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.post.domain.PostDeleteEvent;
import com.mallang.post.domain.PostId;
import com.mallang.statistics.statistic.source.PostViewHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 조회이력 이벤트 핸들러 (PostViewHistoryEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostViewHistoryEventHandlerTest {

    private final PostViewHistoryRepository postViewHistoryRepository = mock(PostViewHistoryRepository.class);
    private final PostViewHistoryEventHandler postViewHistoryEventHandler = new PostViewHistoryEventHandler(
            postViewHistoryRepository
    );

    @Test
    void 포스트_제거_이벤트를_받아_해당_포스트의_조회_이력을_제거한다() {
        // given
        PostId postId = new PostId(1L, 1L);
        PostDeleteEvent postDeleteEvent = new PostDeleteEvent(postId);

        // when
        postViewHistoryEventHandler.deletePostViewHistory(postDeleteEvent);

        // then
        then(postViewHistoryRepository)
                .should(times(1))
                .deleteAllByPostId(postId);
    }
}
