package com.mallang.comment.domain;

import static com.mallang.auth.MemberFixture.동훈;
import static com.mallang.auth.MemberFixture.말랑;
import static com.mallang.auth.MemberFixture.회원;
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
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글(Comment) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentTest {

    private final Member postWriter = 회원(100L, "글 작성자");
    private final Blog blog = new Blog("blog", postWriter);
    private final Post post = Post.builder()
            .writer(postWriter)
            .visibilityPolish(new PostVisibilityPolicy(Visibility.PUBLIC, null))
            .blog(blog)
            .build();
    private final Member member = 말랑(1L);
    private final Member other = 동훈(2L);

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
    class 삭제_시 {

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
            child.delete(null, "1234", commentDeleteService, null);

            // then
            assertThat(child.isDeleted()).isTrue();
            assertThat(child.getParent()).isNull();
            assertThat(parent.getChildren()).isEmpty();
        }

        @Test
        void 댓글_제거_시_자식_댓글이_존재한다면_제거된_상태이나_자식_댓글과_관계는_유지된다() {
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
            parent.delete(member, commentDeleteService, null);

            // then
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
            childComment.delete(member, commentDeleteService, null);

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
            parentComment.delete(member, commentDeleteService, null);

            // when
            childComment.delete(member, commentDeleteService, null);

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
            childComment.delete(member, commentDeleteService, null);

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
            parentComment.delete(member, commentDeleteService, null);

            // then
            assertThat(childComment.getParent()).isEqualTo(parentComment);
            assertThat(parentComment.getChildren()).hasSize(1);
            assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment);
            verify(commentRepository, times(0)).delete(childComment);
            verify(commentRepository, times(0)).delete(parentComment);
        }
    }
}
