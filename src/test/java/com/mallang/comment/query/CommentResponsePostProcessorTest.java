package com.mallang.comment.query;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.comment.query.response.AuthCommentResponse;
import com.mallang.comment.query.response.AuthCommentResponse.WriterResponse;
import com.mallang.comment.query.response.CommentResponse;
import com.mallang.comment.query.response.UnAuthCommentResponse;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 조회 데이터 후처리기 (CommentDataPostProcessor) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentResponsePostProcessorTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final CommentDataPostProcessor commentDataPostProcessor =
            new CommentDataPostProcessor(postRepository);

    @Test
    void 삭제된_댓글들_처리() {
        // given
        List<CommentResponse> commentResponses = List.of(
                UnAuthCommentResponse.builder()
                        .content("삭제 댓글1")
                        .deleted(true)
                        .build(),
                UnAuthCommentResponse.builder()
                        .content("댓글2")
                        .deleted(false)
                        .build(),
                AuthCommentResponse.builder()
                        .content("댓글3")
                        .deleted(false)
                        .build(),
                AuthCommentResponse.builder()
                        .content("삭제 댓글4")
                        .deleted(true)
                        .build()
        );

        // when
        List<CommentResponse> commentData = commentDataPostProcessor.processDeleted(commentResponses);

        // then
        assertThat(commentData)
                .extracting(CommentResponse::getContent)
                .containsExactly("삭제된 댓글입니다.", "댓글2", "댓글3", "삭제된 댓글입니다.");
    }

    @Nested
    class 비공개_댓글들_처리_시 {

        @Test
        void 글_작성자라면_모두_볼_수_있다() {
            // given
            Post post = mock(Post.class);
            given(post.getWriter()).willReturn(깃허브_동훈(1L));
            given(postRepository.getById(any(), any())).willReturn(post);
            List<CommentResponse> commentResponses = List.of(
                    AuthCommentResponse.builder()
                            .content("비공개 댓글")
                            .secret(true)
                            .build(),
                    AuthCommentResponse.builder()
                            .content("공개 댓글")
                            .secret(false)
                            .build(),
                    AuthCommentResponse.builder()
                            .content("비공개 댓글2")
                            .secret(true)
                            .children(List.of(
                                    AuthCommentResponse.builder()
                                            .content("비공개 대댓글")
                                            .secret(true)
                                            .build(),
                                    AuthCommentResponse.builder()
                                            .content("공개 대댓글")
                                            .secret(false)
                                            .build()
                            )).build()
            );

            // when
            List<CommentResponse> commentData = commentDataPostProcessor.processSecret(commentResponses, 1L, "blog",
                    1L);

            // then
            assertThat(commentData)
                    .extracting(CommentResponse::getContent)
                    .containsExactly("비공개 댓글", "공개 댓글", "비공개 댓글2");
            assertThat(commentData.get(2).getChildren())
                    .extracting(CommentResponse::getContent)
                    .containsExactly("비공개 대댓글", "공개 대댓글");
        }

        @Test
        void 포스트_작성자가_아니면_비공개_댓글은_숨겨진다() {
            // given
            Post post = mock(Post.class);
            given(post.getWriter()).willReturn(깃허브_동훈(1L));
            given(postRepository.getById(any(), any())).willReturn(post);
            List<CommentResponse> commentResponses = List.of(
                    AuthCommentResponse.builder()
                            .content("비공개 댓글")
                            .writer(new WriterResponse(100L, null, null))
                            .secret(true)
                            .build(),
                    AuthCommentResponse.builder()
                            .content("공개 댓글")
                            .writer(new WriterResponse(100L, null, null))
                            .secret(false)
                            .build(),
                    AuthCommentResponse.builder()
                            .content("비공개 댓글2")
                            .writer(new WriterResponse(100L, null, null))
                            .secret(true)
                            .children(List.of(
                                    AuthCommentResponse.builder()
                                            .content("비공개 대댓글")
                                            .writer(new WriterResponse(100L, null, null))
                                            .secret(true)
                                            .build(),
                                    AuthCommentResponse.builder()
                                            .writer(new WriterResponse(100L, null, null))
                                            .content("공개 대댓글")
                                            .secret(false)
                                            .build()
                            )).build()
            );

            // when
            List<CommentResponse> commentData = commentDataPostProcessor
                    .processSecret(commentResponses, 1L, "blog", null);

            // then
            assertThat(commentData)
                    .extracting(CommentResponse::getContent)
                    .containsExactly("비밀 댓글입니다.", "공개 댓글", "비밀 댓글입니다.");
            assertThat(commentData.get(2).getChildren())
                    .extracting(CommentResponse::getContent)
                    .containsExactly("비밀 댓글입니다.", "공개 대댓글");
        }

        @Test
        void 포스트_작성자가_아니더라도_자신이_쓴_비밀_댓글은_조회할_수_있다() {
            // given
            Post post = mock(Post.class);
            given(post.getWriter()).willReturn(깃허브_동훈(1L));
            given(postRepository.getById(any(), any())).willReturn(post);
            List<CommentResponse> commentResponses = List.of(
                    AuthCommentResponse.builder()
                            .content("비공개 댓글1")
                            .writer(new WriterResponse(100L, null, null))
                            .secret(true)
                            .build(),
                    AuthCommentResponse.builder()
                            .writer(new WriterResponse(2L, null, null))
                            .content("비공개 댓글2")
                            .secret(true)
                            .build(),
                    AuthCommentResponse.builder()
                            .content("비공개 댓글3")
                            .writer(new WriterResponse(100L, null, null))
                            .secret(true)
                            .children(List.of(
                                    AuthCommentResponse.builder()
                                            .content("비공개 대댓글")
                                            .writer(new WriterResponse(2L, null, null))
                                            .secret(true)
                                            .build(),
                                    AuthCommentResponse.builder()
                                            .content("비공개 대댓글2")
                                            .writer(new WriterResponse(100L, null, null))
                                            .secret(true)
                                            .build()
                            )).build()
            );

            // when
            List<CommentResponse> commentData = commentDataPostProcessor
                    .processSecret(commentResponses, 1L, "blog", 2L);

            // then
            assertThat(commentData)
                    .extracting(CommentResponse::getContent)
                    .containsExactly("비밀 댓글입니다.", "비공개 댓글2", "비밀 댓글입니다.");
            assertThat(commentData.get(2).getChildren())
                    .extracting(CommentResponse::getContent)
                    .containsExactly("비공개 대댓글", "비밀 댓글입니다.");
        }
    }
}
