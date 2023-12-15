package com.mallang.comment.application;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.comment.domain.CommentRepository;
import com.mallang.post.domain.PostDeleteEvent;
import com.mallang.post.domain.PostId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 이벤트 핸들러 (CommentEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentEventHandlerTest {

    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final CommentEventHandler commentEventHandler = new CommentEventHandler(commentRepository);

    @Nested
    class 포스트_삭제_이벤트를_받아 {

        @Test
        void 해당_포스트에_달린_댓글들을_모두_제거한다() {
            // given
            PostId postId = new PostId(1L, 2L);

            // when
            commentEventHandler.deleteCommentsFromPost(new PostDeleteEvent(postId));

            // then
            then(commentRepository)
                    .should(times(1))
                    .deleteAllByPostId(postId);
        }
    }
}
