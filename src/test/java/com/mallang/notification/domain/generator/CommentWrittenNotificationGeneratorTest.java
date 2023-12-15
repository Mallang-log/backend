package com.mallang.notification.domain.generator;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.auth.OauthMemberFixture.깃허브_회원;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.comment.CommentFixture.authComment;
import static com.mallang.comment.CommentFixture.unAuthComment;
import static com.mallang.post.PostFixture.publicPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.CommentWrittenEvent;
import com.mallang.comment.domain.UnAuthComment;
import com.mallang.common.domain.DomainEvent;
import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.type.CommentWrittenNotification;
import com.mallang.notification.domain.type.CommentWrittenNotification.Type;
import com.mallang.post.domain.Post;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 작성 알림 변환기 (CommentWrittenNotificationGenerator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentWrittenNotificationGeneratorTest {

    private final CommentRepository repository = mock(CommentRepository.class);
    private final CommentWrittenNotificationGenerator generator = new CommentWrittenNotificationGenerator(repository);
    private final Member mallang = 깃허브_말랑(1L);
    private final Member donghun = 깃허브_동훈(2L);
    private final Member other = 깃허브_회원(3L, "other");
    private final Blog mallangBlog = mallangBlog(3L, mallang);
    private final Post mallangPost = publicPost(1L, mallangBlog);

    @Test
    void 댓글_작성_이벤트를_변환할_수_있다() {
        // given
        CommentWrittenEvent event = mock(CommentWrittenEvent.class);

        // when & then
        assertThat(generator.canGenerateFrom(event)).isTrue();
    }

    @Test
    void 댓글_작성_이벤트가_아니면_변환할_수_없다() {
        // given
        DomainEvent<?> event = mock(DomainEvent.class);

        // when & then
        assertThat(generator.canGenerateFrom(event)).isFalse();
    }

    @Nested
    class 댓글_작성에_대한_이벤트인_경우 {

        @Test
        void 블로그_주인에게만_알림이_생성된다() {
            // given
            AuthComment comment = authComment(1L, "동훈이 댓글", mallangPost, null, true, donghun);
            UnAuthComment unAuthComment = unAuthComment(2L, "비인증 댓글임", mallangPost, null, "Hi", "1234");
            given(repository.getById(comment.getId())).willReturn(comment);
            given(repository.getById(unAuthComment.getId())).willReturn(unAuthComment);
            CommentWrittenEvent authWrittenEvent = new CommentWrittenEvent(comment);
            CommentWrittenEvent unAuthWrittenEvent = new CommentWrittenEvent(unAuthComment);

            // when
            List<Notification> byAuthed = generator.generate(authWrittenEvent);
            List<Notification> byUnAuthed = generator.generate(unAuthWrittenEvent);

            // then
            var expectedByAuth = List.of(new CommentWrittenNotification(
                    mallang.getId(),
                    Type.COMMENT,
                    mallangPost.getId(),
                    null,
                    null,
                    null,
                    comment.getId(),
                    donghun.getId(),
                    donghun.getNickname(),
                    donghun.getProfileImageUrl(),
                    comment.getContent()
            ));
            var expectedByUnAuth = List.of(new CommentWrittenNotification(
                    mallang.getId(),
                    Type.COMMENT,
                    mallangPost.getId(),
                    null,
                    null,
                    null,
                    unAuthComment.getId(),
                    null,
                    "Hi",
                    null,
                    unAuthComment.getContent()
            ));
            assertThat(byAuthed).usingRecursiveComparison()
                    .isEqualTo(expectedByAuth);
            assertThat(byUnAuthed).usingRecursiveComparison()
                    .isEqualTo(expectedByUnAuth);
        }

        @Test
        void 블로그_주인이_작성항_댓글의_경우_알림이_생성되지_않는다() {
            // given
            AuthComment comment = authComment(1L, "블로그 주인 댓글", mallangPost, null, true, mallang);
            CommentWrittenEvent authWrittenEvent = new CommentWrittenEvent(comment);
            given(repository.getById(comment.getId())).willReturn(comment);

            // when
            List<Notification> byBlogOwner = generator.generate(authWrittenEvent);

            // then
            assertThat(byBlogOwner).isEmpty();
        }
    }

    @Nested
    class 대댓글_작성에_대한_이벤트인_경우 {

        @Nested
        class 부모_댓글이_비인증_댓글인_경우 {

            @Test
            void 블로그_주인에게_대댓글이_달렸다는_알림이_생성된다() {
                // given
                UnAuthComment parent = unAuthComment(1L, "비인증 댓글임", mallangPost, null, "Hi", "1234");
                AuthComment reply = authComment(2L, "동훈이 대댓글", mallangPost, parent, true, donghun);
                given(repository.getById(parent.getId())).willReturn(parent);
                given(repository.getById(reply.getId())).willReturn(reply);
                CommentWrittenEvent event = new CommentWrittenEvent(reply);

                // when
                List<Notification> byReply = generator.generate(event);

                // then
                var expectedByAuth = List.of(new CommentWrittenNotification(
                        mallang.getId(),
                        Type.COMMENT_REPLY,
                        mallangPost.getId(),
                        parent.getId(),
                        null,
                        "Hi",
                        reply.getId(),
                        donghun.getId(),
                        donghun.getNickname(),
                        donghun.getProfileImageUrl(),
                        reply.getContent()
                ));
                assertThat(byReply).usingRecursiveComparison()
                        .isEqualTo(expectedByAuth);
            }

            @Test
            void 대댓글_작성자가_블로그_주인인_경우_알림이_생성되지_않는다() {
                // given
                UnAuthComment parent = unAuthComment(1L, "비인증 댓글임", mallangPost, null, "Hi", "1234");
                AuthComment reply = authComment(2L, "블로그 주인 대댓글", mallangPost, parent, true, mallang);
                given(repository.getById(parent.getId())).willReturn(parent);
                given(repository.getById(reply.getId())).willReturn(reply);
                CommentWrittenEvent event = new CommentWrittenEvent(reply);

                // when
                List<Notification> byReply = generator.generate(event);

                // then
                assertThat(byReply).isEmpty();
            }
        }

        @Nested
        class 부모_댓글이_인증된_댓글인_경우 {

            @Test
            void 대댓글_작성자와_부모_댓글_작성자와_블로그_주인이_모두_다른_경우_부모_댓글_작성자와_블로그_주인에게_알림이_전송된다() {
                // given
                AuthComment parent = authComment(1L, "동훈이 댓글", mallangPost, null, true, donghun);
                UnAuthComment reply = unAuthComment(2L, "비인증 대댓글임", mallangPost, parent, "Hi", "1234");
                given(repository.getById(parent.getId())).willReturn(parent);
                given(repository.getById(reply.getId())).willReturn(reply);
                CommentWrittenEvent event = new CommentWrittenEvent(reply);

                // when
                List<Notification> byReply = generator.generate(event);

                // then
                var expected = List.of(
                        new CommentWrittenNotification(
                                donghun.getId(),
                                Type.COMMENT_REPLY,
                                mallangPost.getId(),
                                parent.getId(),
                                donghun.getId(),
                                donghun.getNickname(),
                                reply.getId(),
                                null,
                                "Hi",
                                null,
                                reply.getContent()
                        ),
                        new CommentWrittenNotification(
                                mallang.getId(),
                                Type.COMMENT_REPLY,
                                mallangPost.getId(),
                                parent.getId(),
                                donghun.getId(),
                                donghun.getNickname(),
                                reply.getId(),
                                null,
                                "Hi",
                                null,
                                reply.getContent()
                        )
                );
                assertThat(byReply)
                        .usingRecursiveComparison()
                        .ignoringCollectionOrder()
                        .isEqualTo(expected);
            }

            @Test
            void 부모_댓글_작성자가_블로그_주인이라면_블로그_주인에게_하나의_알림만_전송된다() {
                // given
                AuthComment parent = authComment(1L, "블로그 주인 댓글", mallangPost, null, true, mallang);
                AuthComment reply = authComment(2L, "동훈이 대댓글임", mallangPost, parent, true, donghun);
                given(repository.getById(parent.getId())).willReturn(parent);
                given(repository.getById(reply.getId())).willReturn(reply);
                CommentWrittenEvent event = new CommentWrittenEvent(reply);

                // when
                List<Notification> byReply = generator.generate(event);

                // then
                var expected = List.of(
                        new CommentWrittenNotification(
                                mallang.getId(),
                                Type.COMMENT_REPLY,
                                mallangPost.getId(),
                                parent.getId(),
                                mallang.getId(),
                                mallang.getNickname(),
                                reply.getId(),
                                donghun.getId(),
                                donghun.getNickname(),
                                donghun.getProfileImageUrl(),
                                reply.getContent()
                        )
                );
                assertThat(byReply)
                        .usingRecursiveComparison()
                        .ignoringCollectionOrder()
                        .isEqualTo(expected);
            }

            @Test
            void 대댓글_작성자가_블로그_주인이라면_부모_댓글_작성자에게만_알림이_전송된다() {
                // given
                AuthComment parent = authComment(1L, "동훈이 댓글", mallangPost, null, true, donghun);
                AuthComment reply = authComment(2L, "주인 대댓글임", mallangPost, parent, true, mallang);
                given(repository.getById(parent.getId())).willReturn(parent);
                given(repository.getById(reply.getId())).willReturn(reply);
                CommentWrittenEvent event = new CommentWrittenEvent(reply);

                // when
                List<Notification> byReply = generator.generate(event);

                // then
                var expected = List.of(
                        new CommentWrittenNotification(
                                donghun.getId(),
                                Type.COMMENT_REPLY,
                                mallangPost.getId(),
                                parent.getId(),
                                donghun.getId(),
                                donghun.getNickname(),
                                reply.getId(),
                                mallang.getId(),
                                mallang.getNickname(),
                                mallang.getProfileImageUrl(),
                                reply.getContent()
                        )
                );
                assertThat(byReply)
                        .usingRecursiveComparison()
                        .ignoringCollectionOrder()
                        .isEqualTo(expected);
            }

            @Test
            void 대댓글_작성자와_부모_댓글_작성자가_일치하다면_블로그_주인에게만_알림이_전송된다() {
                // given
                AuthComment parent = authComment(1L, "동훈이 댓글", mallangPost, null, true, donghun);
                AuthComment reply = authComment(2L, "동훈이 대댓글임", mallangPost, parent, true, donghun);
                given(repository.getById(parent.getId())).willReturn(parent);
                given(repository.getById(reply.getId())).willReturn(reply);
                CommentWrittenEvent event = new CommentWrittenEvent(reply);

                // when
                List<Notification> byReply = generator.generate(event);

                // then
                var expected = List.of(
                        new CommentWrittenNotification(
                                mallang.getId(),
                                Type.COMMENT_REPLY,
                                mallangPost.getId(),
                                parent.getId(),
                                donghun.getId(),
                                donghun.getNickname(),
                                reply.getId(),
                                donghun.getId(),
                                donghun.getNickname(),
                                donghun.getProfileImageUrl(),
                                reply.getContent()
                        )
                );
                assertThat(byReply)
                        .usingRecursiveComparison()
                        .ignoringCollectionOrder()
                        .isEqualTo(expected);
            }

            @Test
            void 블로그_주인이_댓글_작성자이며_대댓글_작성자인_경우_알림이_생성되지_않는다() {
                // given
                AuthComment parent = authComment(1L, "블로그 주인 댓글", mallangPost, null, true, mallang);
                AuthComment reply = authComment(2L, "블로그 주인 대댓글임", mallangPost, parent, true, mallang);
                given(repository.getById(parent.getId())).willReturn(parent);
                given(repository.getById(reply.getId())).willReturn(reply);
                CommentWrittenEvent event = new CommentWrittenEvent(reply);

                // when
                List<Notification> byReply = generator.generate(event);

                // then
                assertThat(byReply).isEmpty();
            }
        }
    }
}
