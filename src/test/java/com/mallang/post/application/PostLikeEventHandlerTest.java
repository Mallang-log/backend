package com.mallang.post.application;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.post.domain.PostDeleteEvent;
import com.mallang.post.domain.PostId;
import com.mallang.post.domain.like.PostLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 좋아요 이벤트 핸들러 (PostLikeEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostLikeEventHandlerTest {

    private final PostLikeRepository postLikeRepository = mock(PostLikeRepository.class);
    private final PostLikeEventHandler postLikeEventHandler = new PostLikeEventHandler(postLikeRepository);

    @Nested
    class 포스트_삭제_이벤트를_받아 {

        @Test
        void 해당_포스트에_눌린_좋아요를_모두_제거한다() {
            // given
            PostId postId = new PostId(1L, 1L);
            PostDeleteEvent postDeleteEvent = new PostDeleteEvent(postId);

            // when
            postLikeEventHandler.deletePostLike(postDeleteEvent);

            // then
            then(postLikeRepository)
                    .should(times(1))
                    .deleteAllByPostId(postId);
        }
    }
}
