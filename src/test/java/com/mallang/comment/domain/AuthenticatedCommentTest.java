package com.mallang.comment.domain;

import static com.mallang.member.MemberFixture.동훈;
import static com.mallang.member.MemberFixture.말랑;
import static com.mallang.member.MemberFixture.회원;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import com.mallang.blog.domain.Blog;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("인증된 사용자의 댓글(AuthenticatedComment) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthenticatedCommentTest {

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
    class 작성_시 {

        @Test
        void 비밀_댓글은_로그인한_사용자만_작성_가능하다() {
            // when
            AuthenticatedComment comment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();

            // then
            assertThat(comment.isSecret()).isTrue();
        }

        @Test
        void 공개_댓글을_작성할_수_있다() {
            // when
            AuthenticatedComment auth = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(false)
                    .build();

            // then
            assertThat(auth.getContent()).isEqualTo("내용");
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 자신의_댓글이_아닌_경우_예외() {
            // given
            AuthenticatedComment comment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(postWriter)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.update(other, "수정", true, null)
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 댓글을_변경한다() {
            // given
            AuthenticatedComment comment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(true)
                    .writer(member)
                    .build();

            // when
            comment.update(member, "update", true, null);

            // then
            assertThat(comment.getContent()).isEqualTo("update");
        }

        @ParameterizedTest(name = "(공개여부({0}) -> 공개여부({1}))")
        @CsvSource(
                value = {
                        "true -> false",
                        "false -> true",
                }, delimiterString = " -> ")
        void 로그인한_유저는_비공개_여부도_변경할_수_있다(boolean before, boolean after) {
            // given
            AuthenticatedComment comment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(before)
                    .writer(member)
                    .build();

            // when
            comment.update(member, "변경", after, null);

            // then
            assertThat(comment.getContent()).isEqualTo("변경");
            assertThat(comment.isSecret()).isEqualTo(after);
        }
    }

    @Nested
    class 삭제_시 {

        private final CommentRepository commentRepository = mock(CommentRepository.class);
        private final CommentDeleteService commentDeleteService = new CommentDeleteService(commentRepository);

        @Test
        void 자신의_댓글인_경우_제거할_수_있다() {
            // given
            AuthenticatedComment comment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();

            // when & then
            assertDoesNotThrow(() ->
                    comment.delete(member, commentDeleteService, null)
            );
        }

        @Test
        void 자신의_댓글이_아닌_경우_예외() {
            // given
            AuthenticatedComment comment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.delete(other, commentDeleteService, null)
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 포스트_작성자는_모든_댓글_삭제_가능하다() {
            // given
            AuthenticatedComment comment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();

            AuthenticatedComment secretComment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(true)
                    .writer(member)
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                comment.delete(postWriter, commentDeleteService, null);
                secretComment.delete(postWriter, commentDeleteService, null);
            });
        }
    }
}
