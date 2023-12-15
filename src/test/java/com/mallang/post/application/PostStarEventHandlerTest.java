package com.mallang.post.application;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.post.domain.PostDeleteEvent;
import com.mallang.post.domain.PostId;
import com.mallang.post.domain.star.PostStarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 즐겨찾기 이벤트 핸들러 (PostStarEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostStarEventHandlerTest {

    private final PostStarRepository postStarRepository = mock(PostStarRepository.class);
    private final PostStarEventHandler postStarEventHandler = new PostStarEventHandler(postStarRepository);

    @Nested
    class 포스트_삭제_이벤트를_받아 {

        @Test
        void 해당_포스트에_눌린_좋아요를_모두_제거한다() {
            // given
            PostId postId = new PostId(1L, 2L);
            PostDeleteEvent postDeleteEvent = new PostDeleteEvent(postId);

            // when
            postStarEventHandler.deletePostLike(postDeleteEvent);

            // then
            then(postStarRepository)
                    .should(times(1))
                    .deleteAllByPostId(postId);
        }
    }
}
