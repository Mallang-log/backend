package com.mallang.comment.query;

import static com.mallang.auth.MemberFixture.동훈;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.comment.query.data.AuthenticatedCommentData;
import com.mallang.comment.query.data.AuthenticatedCommentData.WriterData;
import com.mallang.comment.query.data.CommentData;
import com.mallang.comment.query.data.UnAuthenticatedCommentData;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 조회 데이터 후처리기(CommentDataPostProcessor) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentDataPostProcessorTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final CommentDataPostProcessor commentDataPostProcessor =
            new CommentDataPostProcessor(postRepository);

    @Test
    void 삭제된_댓글들_처리() {
        // given
        List<CommentData> commentDatas = List.of(
                UnAuthenticatedCommentData.builder()
                        .content("삭제 댓글1")
                        .deleted(true)
                        .build(),
                UnAuthenticatedCommentData.builder()
                        .content("댓글2")
                        .deleted(false)
                        .build(),
                AuthenticatedCommentData.builder()
                        .content("댓글3")
                        .deleted(false)
                        .build(),
                AuthenticatedCommentData.builder()
                        .content("삭제 댓글4")
                        .deleted(true)
                        .build()
        );

        // when
        List<CommentData> commentData = commentDataPostProcessor.processDeleted(commentDatas);

        // then
        assertThat(commentData)
                .extracting(CommentData::getContent)
                .containsExactly("삭제된 댓글입니다.", "댓글2", "댓글3", "삭제된 댓글입니다.");
    }

    @Nested
    class 비공개_댓글들_처리_시 {

        @Test
        void 글_작성자라면_모두_볼_수_있다() {
            // given
            Post post = mock(Post.class);
            given(post.getWriter()).willReturn(동훈(1L));
            given(postRepository.getById(1L)).willReturn(post);
            List<CommentData> commentDatas = List.of(
                    AuthenticatedCommentData.builder()
                            .content("비공개 댓글")
                            .secret(true)
                            .build(),
                    AuthenticatedCommentData.builder()
                            .content("공개 댓글")
                            .secret(false)
                            .build(),
                    AuthenticatedCommentData.builder()
                            .content("비공개 댓글2")
                            .secret(true)
                            .children(List.of(
                                    AuthenticatedCommentData.builder()
                                            .content("비공개 대댓글")
                                            .secret(true)
                                            .build(),
                                    AuthenticatedCommentData.builder()
                                            .content("공개 대댓글")
                                            .secret(false)
                                            .build()
                            )).build()
            );

            // when
            List<CommentData> commentData = commentDataPostProcessor.processSecret(commentDatas, 1L, 1L);

            // then
            assertThat(commentData)
                    .extracting(CommentData::getContent)
                    .containsExactly("비공개 댓글", "공개 댓글", "비공개 댓글2");
            assertThat(commentData.get(2).getChildren())
                    .extracting(CommentData::getContent)
                    .containsExactly("비공개 대댓글", "공개 대댓글");
        }

        @Test
        void 포스트_작성자가_아니면_비공개_댓글은_숨겨진다() {
            // given
            Post post = mock(Post.class);
            given(post.getWriter()).willReturn(동훈(1L));
            given(postRepository.getById(1L)).willReturn(post);
            List<CommentData> commentDatas = List.of(
                    AuthenticatedCommentData.builder()
                            .content("비공개 댓글")
                            .writerData(new WriterData(100L, null, null))
                            .secret(true)
                            .build(),
                    AuthenticatedCommentData.builder()
                            .content("공개 댓글")
                            .writerData(new WriterData(100L, null, null))
                            .secret(false)
                            .build(),
                    AuthenticatedCommentData.builder()
                            .content("비공개 댓글2")
                            .writerData(new WriterData(100L, null, null))
                            .secret(true)
                            .children(List.of(
                                    AuthenticatedCommentData.builder()
                                            .content("비공개 대댓글")
                                            .writerData(new WriterData(100L, null, null))
                                            .secret(true)
                                            .build(),
                                    AuthenticatedCommentData.builder()
                                            .writerData(new WriterData(100L, null, null))
                                            .content("공개 대댓글")
                                            .secret(false)
                                            .build()
                            )).build()
            );

            // when
            List<CommentData> commentData = commentDataPostProcessor.processSecret(commentDatas, 1L, null);

            // then
            assertThat(commentData)
                    .extracting(CommentData::getContent)
                    .containsExactly("비밀 댓글입니다.", "공개 댓글", "비밀 댓글입니다.");
            assertThat(commentData.get(2).getChildren())
                    .extracting(CommentData::getContent)
                    .containsExactly("비밀 댓글입니다.", "공개 대댓글");
        }

        @Test
        void 포스트_작성자가_아니더라도_자신이_쓴_비밀_댓글은_조회할_수_있다() {
            // given
            Post post = mock(Post.class);
            given(post.getWriter()).willReturn(동훈(1L));
            given(postRepository.getById(1L)).willReturn(post);
            List<CommentData> commentDatas = List.of(
                    AuthenticatedCommentData.builder()
                            .content("비공개 댓글1")
                            .writerData(new WriterData(100L, null, null))
                            .secret(true)
                            .build(),
                    AuthenticatedCommentData.builder()
                            .writerData(new WriterData(2L, null, null))
                            .content("비공개 댓글2")
                            .secret(true)
                            .build(),
                    AuthenticatedCommentData.builder()
                            .content("비공개 댓글3")
                            .writerData(new WriterData(100L, null, null))
                            .secret(true)
                            .children(List.of(
                                    AuthenticatedCommentData.builder()
                                            .content("비공개 대댓글")
                                            .writerData(new WriterData(2L, null, null))
                                            .secret(true)
                                            .build(),
                                    AuthenticatedCommentData.builder()
                                            .content("비공개 대댓글2")
                                            .writerData(new WriterData(100L, null, null))
                                            .secret(true)
                                            .build()
                            )).build()
            );

            // when
            List<CommentData> commentData = commentDataPostProcessor.processSecret(commentDatas, 1L, 2L);

            // then
            assertThat(commentData)
                    .extracting(CommentData::getContent)
                    .containsExactly("비밀 댓글입니다.", "비공개 댓글2", "비밀 댓글입니다.");
            assertThat(commentData.get(2).getChildren())
                    .extracting(CommentData::getContent)
                    .containsExactly("비공개 대댓글", "비밀 댓글입니다.");
        }
    }
}
