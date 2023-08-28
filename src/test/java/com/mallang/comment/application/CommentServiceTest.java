package com.mallang.comment.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.comment.application.command.WriteAuthenticatedCommentCommand;
import com.mallang.member.MemberServiceTestHelper;
import com.mallang.post.application.PostServiceTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DisplayName("댓글 서비스(CommentService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentServiceTest {

    @DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
    @SpringBootTest
    @Nested
    class 댓글_작성_시 {

        @Autowired
        private MemberServiceTestHelper memberServiceTestHelper;

        @Autowired
        private PostServiceTestHelper postServiceTestHelper;

        @Autowired
        private CommentService commentService;

        @Test
        void 로그인한_사용자가_댓글을_작성한다() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long 댓글작성자_ID = memberServiceTestHelper.회원을_저장한다("댓글작성자");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(말랑_ID, "포스트", "내용");
            WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                    .postId(포스트_ID)
                    .content("댓글입니다.")
                    .memberId(댓글작성자_ID)
                    .secret(false)
                    .build();

            // when
            Long 댓글_ID = commentService.write(command);

            // then
            assertThat(댓글_ID).isNotNull();
        }

        @Test
        void 로그인한_사용자는_비밀_댓글_작성이_가능하다() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long 댓글작성자_ID = memberServiceTestHelper.회원을_저장한다("댓글작성자");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(말랑_ID, "포스트", "내용");
            WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                    .postId(포스트_ID)
                    .content("댓글입니다.")
                    .memberId(댓글작성자_ID)
                    .secret(true)
                    .build();

            // when
            Long 댓글_ID = commentService.write(command);

            // then
            assertThat(댓글_ID).isNotNull();
        }
    }
}
