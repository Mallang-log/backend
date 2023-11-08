package com.mallang.comment.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogName;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.common.EventTestHelper;
import com.mallang.common.ServiceTest;
import com.mallang.member.MemberServiceTestHelper;
import com.mallang.post.application.PostServiceTestHelper;
import com.mallang.post.domain.PostDeleteEvent;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@DisplayName("댓글 이벤트 핸들러(CommentEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class CommentEventHandlerTest {

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private BlogServiceTestHelper blogServiceTestHelper;

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Autowired
    private CommentServiceTestHelper commentServiceTestHelper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private EventTestHelper eventTestHelper;

    @Nested
    class 포스트_삭제_이벤트를_받아 {

        @Test
        void 해당_포스트에_달린_댓글들을_모두_제거한다() {
            // given
            Long memberId = memberServiceTestHelper.회원을_저장한다("말랑");
            Long ohterMemberId = memberServiceTestHelper.회원을_저장한다("ohter");
            Blog blog = blogServiceTestHelper.블로그_개설(memberId, "mallang-log");
            BlogName blogName = blog.getName();
            Long postId1 = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "제목", "내용");
            Long postId2 = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "제목2", "내용1");
            Long post1Comment1 = commentServiceTestHelper.댓글을_작성한다(postId1, "댓1", true, ohterMemberId);
            commentServiceTestHelper.대댓글을_작성한다(postId1, "댓1", true, memberId, post1Comment1);
            commentServiceTestHelper.비인증_대댓글을_작성한다(postId1, "댓1", "익1", "1234", post1Comment1);
            commentServiceTestHelper.비인증_댓글을_작성한다(postId1, "댓1", "익2", "12345");

            Long post2Comment1 = commentServiceTestHelper.댓글을_작성한다(postId2, "댓1", true, ohterMemberId);

            // when
            publisher.publishEvent(new PostDeleteEvent(postId1, blog.getId()));

            // then
            List<Comment> all = commentRepository.findAll();
            boolean nonDeleteCommentExist = all.stream()
                    .anyMatch(it -> (it.getPost().getId().equals(postId1)));
            assertThat(nonDeleteCommentExist).isFalse();
            assertThat(eventTestHelper.이벤트_발생_횟수(PostDeleteEvent.class)).isEqualTo(1);
        }
    }
}
