package com.mallang.notification.query.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.type.CommentWrittenNotification;
import com.mallang.notification.domain.type.CommentWrittenNotification.Type;
import com.mallang.notification.query.response.CommentWrittenNotificationListResponse;
import com.mallang.post.domain.PostId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("댓글 작성 알림 응답 매퍼 (CommentWrittenNotificationResponseMapper) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentWrittenNotificationResponseMapperTest {

    private final CommentWrittenNotificationResponseMapper mapper = new CommentWrittenNotificationResponseMapper();

    @Test
    void 댓글_작성_알림을_지원한다() {
        // given
        var notification = mock(CommentWrittenNotification.class);

        // when & then
        assertThat(mapper.support(notification)).isTrue();
    }

    @Test
    void 댓글_작성_알림이_아니면_지원하지_않는다() {
        // given
        var notification = mock(Notification.class);

        // when & then
        assertThat(mapper.support(notification)).isFalse();
    }

    @Test
    void 댓글_작성_알림을_응답_객체로_변환한다() {
        // given
        var notification = new CommentWrittenNotification(
                1L,
                Type.COMMENT,
                new PostId(2L, 3L),
                null,
                null,
                null,
                6L,
                7L,
                "comment writer",
                "image url",
                "대댓글"
        );
        ReflectionTestUtils.setField(notification, "id", 8L);

        // when
        var response = (CommentWrittenNotificationListResponse) mapper.mapToResponse(notification);

        // then
        assertThat(response.id()).isEqualTo(8L);
        assertThat(response.targetMemberId()).isEqualTo(1L);
        assertThat(response.commentType()).isEqualTo(Type.COMMENT);
        assertThat(response.postId()).isEqualTo(2L);
        assertThat(response.postOwnerBlogId()).isEqualTo(3L);
        assertThat(response.parentCommentId()).isNull();
        assertThat(response.parentCommentWriterId()).isNull();
        assertThat(response.parentCommentWriterName()).isNull();
        assertThat(response.commentId()).isEqualTo(6L);
        assertThat(response.commentWriterId()).isEqualTo(7L);
        assertThat(response.commentWriterName()).isEqualTo("comment writer");
        assertThat(response.commentWriterImageUrl()).isEqualTo("image url");
        assertThat(response.message()).isEqualTo("대댓글");
    }

    @Test
    void 댓글_작성_알림을_응답_객체로_변환한다_대댓글의_경우() {
        // given
        var notification = new CommentWrittenNotification(
                1L,
                Type.COMMENT_REPLY,
                new PostId(2L, 3L),
                4L,
                5L,
                "parent writer",
                6L,
                7L,
                "comment writer",
                "image url",
                "대댓글"
        );
        ReflectionTestUtils.setField(notification, "id", 8L);

        // when
        var response = (CommentWrittenNotificationListResponse) mapper.mapToResponse(notification);

        // then
        assertThat(response.id()).isEqualTo(8L);
        assertThat(response.targetMemberId()).isEqualTo(1L);
        assertThat(response.commentType()).isEqualTo(Type.COMMENT_REPLY);
        assertThat(response.postId()).isEqualTo(2L);
        assertThat(response.postOwnerBlogId()).isEqualTo(3L);
        assertThat(response.parentCommentId()).isEqualTo(4L);
        assertThat(response.parentCommentWriterId()).isEqualTo(5L);
        assertThat(response.parentCommentWriterName()).isEqualTo("parent writer");
        assertThat(response.commentId()).isEqualTo(6L);
        assertThat(response.commentWriterId()).isEqualTo(7L);
        assertThat(response.commentWriterName()).isEqualTo("comment writer");
        assertThat(response.commentWriterImageUrl()).isEqualTo("image url");
        assertThat(response.message()).isEqualTo("대댓글");
    }
}
