package com.mallang.comment.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.comment.domain.Comment;
import com.mallang.common.ServiceTest;
import com.mallang.post.domain.PostDeleteEvent;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 이벤트 핸들러(CommentEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentEventHandlerTest extends ServiceTest {

    @Nested
    class 포스트_삭제_이벤트를_받아 {

        @Test
        void 해당_포스트에_달린_댓글들을_모두_제거한다() {
            // given
            Long memberId = 회원을_저장한다("말랑");
            Long otherMemberId = 회원을_저장한다("ohter");
            Long blogId = 블로그_개설(memberId, "mallang-log");
            Long postId1 = 포스트를_저장한다(memberId, blogId, "제목", "내용");
            Long postId2 = 포스트를_저장한다(memberId, blogId, "제목2", "내용1");
            Long post1Comment1 = 댓글을_작성한다(postId1, "댓1", true, otherMemberId);
            대댓글을_작성한다(postId1, "댓1", true, memberId, post1Comment1);
            비인증_대댓글을_작성한다(postId1, "댓1", "익1", "1234", post1Comment1);
            비인증_댓글을_작성한다(postId1, "댓1", "익2", "12345");

            댓글을_작성한다(postId2, "댓1", true, otherMemberId); // no delete

            // when
            publisher.publishEvent(new PostDeleteEvent(postId1));

            // then
            List<Comment> all = commentRepository.findAll();
            boolean nonDeleteCommentExist = all.stream()
                    .anyMatch(it -> (it.getPost().getId().equals(postId1)));
            assertThat(nonDeleteCommentExist).isFalse();
        }
    }
}
