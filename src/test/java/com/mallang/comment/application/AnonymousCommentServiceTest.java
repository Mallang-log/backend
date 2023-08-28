package com.mallang.comment.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.comment.application.command.WriteAnonymousCommentCommand;
import com.mallang.member.MemberServiceTestHelper;
import com.mallang.post.application.PostServiceTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("익명 댓글 서비스(AnonymousCommentService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class AnonymousCommentServiceTest {

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Autowired
    private AnonymousCommentService anonymousCommentService;

    @Nested
    class 댓글_작성_시 {

        @Test
        void 로그인하지_않은_사용자도_댓글을_달_수_있다() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(말랑_ID, "포스트", "내용");
            WriteAnonymousCommentCommand command = WriteAnonymousCommentCommand.builder()
                    .postId(포스트_ID)
                    .content("댓글입니다.")
                    .nickname("익명1")
                    .password("1234")
                    .build();

            // when
            Long 댓글_ID = anonymousCommentService.write(command);

            // then
            assertThat(댓글_ID).isNotNull();
        }
    }
}
