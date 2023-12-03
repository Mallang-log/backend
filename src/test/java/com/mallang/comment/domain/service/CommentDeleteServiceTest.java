package com.mallang.comment.domain.service;

import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.auth.OauthMemberFixture.깃허브_회원;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.UnAuthComment;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostVisibilityPolicy;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("댓글 제거 도메인 서비스 (CommentDeleteService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentDeleteServiceTest {

    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final CommentDeleteService commentDeleteService = new CommentDeleteService(commentRepository);

    private final Member postWriter = 깃허브_회원(1L, "writer");
    private final Blog blog = Blog.builder()
            .name("mallang")
            .owner(postWriter)
            .build();
    private final Post post = Post.builder()
            .blog(blog)
            .writer(postWriter)
            .intro("intro")
            .visibilityPolish(new PostVisibilityPolicy(Visibility.PUBLIC, null))
            .build();
    private final Member member = 깃허브_말랑(1L);

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
        ReflectionTestUtils.setField(parentComment, "id", 1L);
        ReflectionTestUtils.setField(childComment, "id", 2L);

        // when
        commentDeleteService.delete(parentComment);

        // then
        verify(commentRepository, times(0)).delete(childComment);
        verify(commentRepository, times(0)).delete(parentComment);
    }

    @Test
    void 대댓글을_삭제하는_경우_물리적으로_제거된다() {
        // given
        UnAuthComment parentComment = UnAuthComment.builder()
                .content("내용")
                .post(post)
                .nickname("익")
                .password("1234")
                .build();
        UnAuthComment childComment = UnAuthComment.builder()
                .content("to be delete")
                .post(post)
                .nickname("익")
                .password("1234")
                .parent(parentComment)
                .build();
        ReflectionTestUtils.setField(parentComment, "id", 1L);
        ReflectionTestUtils.setField(childComment, "id", 2L);

        // when
        commentDeleteService.delete(childComment);

        // then
        verify(commentRepository, times(1)).delete(childComment);
        verify(commentRepository, times(0)).delete(parentComment);
    }

    @Test
    void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태이며_더이상_존재하는_자식이_없는_경우_부모_댓글도_물리적으로_제거된다() {
        // given
        UnAuthComment parentComment = UnAuthComment.builder()
                .content("내용")
                .post(post)
                .nickname("익")
                .password("1234")
                .build();
        AuthComment childComment = AuthComment.builder()
                .content("to be delete")
                .post(post)
                .writer(member)
                .secret(true)
                .parent(parentComment)
                .build();
        ReflectionTestUtils.setField(parentComment, "id", 1L);
        ReflectionTestUtils.setField(childComment, "id", 2L);
        parentComment.delete(commentDeleteService);

        // when
        commentDeleteService.delete(childComment);

        // then
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
        UnAuthComment childComment = UnAuthComment.builder()
                .content("to be delete")
                .post(post)
                .nickname("익")
                .password("1234")
                .parent(parentComment)
                .build();
        ReflectionTestUtils.setField(parentComment, "id", 1L);
        ReflectionTestUtils.setField(childComment, "id", 2L);

        // when
        commentDeleteService.delete(childComment);

        // then
        verify(commentRepository, times(1)).delete(childComment);
        verify(commentRepository, times(0)).delete(parentComment);
    }

    @Test
    void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태라도_여전히_다른_자식이_존재한다면_부모는_변함없다() {
        // given
        AuthComment parentComment = AuthComment.builder()
                .content("내용")
                .post(post)
                .writer(member)
                .secret(true)
                .build();
        UnAuthComment childComment = UnAuthComment.builder()
                .content("to be delete")
                .post(post)
                .nickname("익")
                .password("1234")
                .parent(parentComment)
                .build();
        UnAuthComment otherChildComment = UnAuthComment.builder()
                .content("to be delete")
                .post(post)
                .nickname("익")
                .password("1234")
                .parent(parentComment)
                .build();
        ReflectionTestUtils.setField(parentComment, "id", 1L);
        ReflectionTestUtils.setField(childComment, "id", 2L);
        ReflectionTestUtils.setField(otherChildComment, "id", 3L);
        parentComment.delete(commentDeleteService);

        // when
        commentDeleteService.delete(childComment);

        // then
        verify(commentRepository, times(1)).delete(childComment);
        verify(commentRepository, times(0)).delete(parentComment);
    }
}
