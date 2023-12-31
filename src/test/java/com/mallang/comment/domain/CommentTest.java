package com.mallang.comment.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.auth.OauthMemberFixture.깃허브_회원;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.CommentDepthConstraintViolationException;
import com.mallang.post.domain.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 (Comment) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentTest {

    private final Member postWriter = 깃허브_회원(100L, "글 작성자");
    private final Blog blog = new Blog("blog", postWriter);
    private final Post post = Post.builder()
            .blog(blog)
            .writer(postWriter)
            .visibility(PUBLIC)
            .password(null)
            .intro("intro")
            .build();
    private final Member member = 깃허브_말랑(1L);
    private final Member other = 깃허브_동훈(2L);

    @Nested
    class 작성_시 {

        @Test
        void 댓글_작성_이벤트가_발행된다() {
            // when
            AuthComment comment1 = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(false)
                    .build();
            UnAuthComment comment2 = UnAuthComment.builder()
                    .post(post)
                    .content("내용")
                    .nickname("mallang")
                    .password("1234")
                    .build();

            // then
            assertThat(comment1.domainEvents())
                    .hasSize(1)
                    .containsExactly(new CommentWrittenEvent(comment1));
            assertThat(comment2.domainEvents())
                    .hasSize(1)
                    .containsExactly(new CommentWrittenEvent(comment2));
        }
    }

    @Nested
    class 대댓글_작성_시 {

        @Test
        void 내_댓글에_대댓글을_달_수_있다() {
            // given
            AuthComment parent = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(false)
                    .build();

            // when
            AuthComment child = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(false)
                    .parent(parent)
                    .build();

            // then
            assertThat(child.getParent()).isEqualTo(parent);
            assertThat(parent.getChildren()).hasSize(1);
            assertThat(parent.getChildren().get(0)).isEqualTo(child);
        }

        @Test
        void 다른_사람의_댓글에_대댓글을_달_수_있다() {
            // given
            AuthComment parent = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(false)
                    .build();

            // when
            AuthComment child = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(other)
                    .secret(false)
                    .parent(parent)
                    .build();

            // then
            assertThat(child.getParent()).isEqualTo(parent);
            assertThat(parent.getChildren()).hasSize(1);
            assertThat(parent.getChildren().get(0)).isEqualTo(child);
        }

        @Test
        void 비밀_댓글에도_대댓글을_달_수_있다() {
            // given
            AuthComment parent = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();

            // when
            UnAuthComment child = UnAuthComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("익")
                    .password("1234")
                    .parent(parent)
                    .build();

            // then
            assertThat(child.getParent()).isEqualTo(parent);
            assertThat(parent.getChildren()).hasSize(1);
            assertThat(parent.getChildren().get(0)).isEqualTo(child);
        }

        @Test
        void 대댓글에_대해서는_댓글을_달_수_없다() {
            // given
            AuthComment parent = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(false)
                    .build();
            UnAuthComment child = UnAuthComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("익")
                    .password("1234")
                    .parent(parent)
                    .build();

            // when
            assertThatThrownBy(() ->
                    AuthComment.builder()
                            .content("내용")
                            .post(post)
                            .writer(member)
                            .secret(false)
                            .parent(child)
                            .build()
            ).isInstanceOf(CommentDepthConstraintViolationException.class);

            // then
            assertThat(child.getChildren()).isEmpty();
        }
    }

    @Nested
    class 제거_시 {

        private final CommentRepository commentRepository = mock(CommentRepository.class);
        private final CommentDeleteService commentDeleteService = new CommentDeleteService(commentRepository);

        @Test
        void 대댓글인_경우_부모_댓글과_관계가_끊어진다() {
            // given
            AuthComment parent = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();
            UnAuthComment child = UnAuthComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .parent(parent)
                    .build();

            // when
            child.delete(commentDeleteService);

            // then
            assertThat(child.isDeleted()).isTrue();
            assertThat(child.getParent()).isNull();
            assertThat(parent.getChildren()).isEmpty();
        }

        @Test
        void 댓글_제거_시_자식_댓글이_존재한다면_댓글을_삭제된_상태로_만들지만_실제로_제거하진_않고_내용만_지운_뒤_자식_댓글과_관계를_유지한다() {
            // given
            AuthComment parent = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();
            UnAuthComment child = UnAuthComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .parent(parent)
                    .build();

            // when
            parent.delete(commentDeleteService);

            // then
            assertThat(parent.getContent()).isEqualTo("삭제된 댓글입니다.");
            assertThat(parent.isDeleted()).isTrue();

            assertThat(child.getParent()).isEqualTo(parent);
            assertThat(parent.getChildren()).hasSize(1);
            assertThat(parent.getChildren().get(0)).isEqualTo(child);
        }

        @Test
        void 대댓글을_삭제하는_경우_부모와의_연관관계는_끊어지며_물리적으로_제거된다() {
            // given
            AuthComment parentComment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();
            AuthComment childComment = AuthComment.builder()
                    .content("to be delete")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .parent(parentComment)
                    .build();
            AuthComment childComment2 = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .parent(parentComment)
                    .build();

            // when
            childComment.delete(commentDeleteService);

            // then
            assertThat(childComment.getParent()).isNull();
            assertThat(parentComment.getChildren()).hasSize(1);
            assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment2);
            verify(commentRepository, times(1)).delete(childComment);
            verify(commentRepository, times(0)).delete(parentComment);
        }

        @Test
        void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태이며_더이상_존재하는_자식이_없는_경우_부모_댓글도_물리적으로_제거된다() {
            // given
            AuthComment parentComment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();
            AuthComment childComment = AuthComment.builder()
                    .content("to be delete")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .parent(parentComment)
                    .build();
            parentComment.delete(commentDeleteService);

            // when
            childComment.delete(commentDeleteService);

            // then
            assertThat(childComment.getParent()).isNull();
            assertThat(parentComment.getChildren()).isEmpty();
            verify(commentRepository, times(1)).delete(childComment);
            verify(commentRepository, times(1)).delete(parentComment);
        }

        @Test
        void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태가_아닌_경우_부모는_변함없다() {
            // given
            AuthComment parentComment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();
            AuthComment childComment = AuthComment.builder()
                    .content("to be delete")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .parent(parentComment)
                    .build();

            // when
            childComment.delete(commentDeleteService);

            // then
            assertThat(childComment.getParent()).isNull();
            assertThat(parentComment.getChildren()).isEmpty();
            verify(commentRepository, times(1)).delete(childComment);
            verify(commentRepository, times(0)).delete(parentComment);
        }

        @Test
        void 자식_댓글이_존재한다면_부모와의_연관관계는_유지되며_논리적으로만_제거시킨다() {
            // given
            AuthComment parentComment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();
            AuthComment childComment = AuthComment.builder()
                    .content("to be delete")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .parent(parentComment)
                    .build();

            // when
            parentComment.delete(commentDeleteService);

            // then
            assertThat(childComment.getParent()).isEqualTo(parentComment);
            assertThat(parentComment.getChildren()).hasSize(1);
            assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment);
            verify(commentRepository, times(0)).delete(childComment);
            verify(commentRepository, times(0)).delete(parentComment);
        }
    }
}
