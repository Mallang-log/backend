package com.mallang.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

import com.mallang.auth.domain.event.MemberSignUpEvent;
import com.mallang.comment.domain.writer.AuthenticatedWriter;
import com.mallang.comment.domain.writer.CommentWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("댓글 이벤트 핸들러(CommentEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DirtiesContext(classMode = BEFORE_CLASS)
@SpringBootTest
class CommentEventHandlerTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private CommentServiceTestHelper commentServiceTestHelper;

    @Nested
    class 회원가입_이벤트를_받아 {

        @Test
        void 인증된_댓글_작성자를_생성한다() {
            // given
            publisher.publishEvent(new MemberSignUpEvent(1L));

            // when
            CommentWriter writer = commentServiceTestHelper.인증된_댓글_작성자를_조회한다(1L);

            // then
            assertThat(writer).isInstanceOf(AuthenticatedWriter.class);
            assertThat(writer.getId()).isNotNull();
            assertThat(((AuthenticatedWriter) writer).getMemberId()).isEqualTo(1L);
        }
    }
}
