package com.mallang.comment.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.comment.query.response.AuthCommentResponse;
import com.mallang.comment.query.response.AuthCommentResponse.WriterResponse;
import com.mallang.comment.query.response.CommentResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 조회 데이터 후처리기 (CommentDataPostProcessor) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentDataPostProcessorTest {

    private final CommentDataPostProcessor commentDataPostProcessor = new CommentDataPostProcessor();

    @Nested
    class 비공개_댓글들_처리_시 {

        @Test
        void 비공개_댓글은_숨겨진다() {
            // given
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
                    .processSecret(commentResponses, 1L);

            // then
            assertThat(commentData)
                    .extracting(CommentResponse::getContent)
                    .containsExactly("비밀 댓글입니다.", "공개 댓글", "비밀 댓글입니다.");
            assertThat(commentData.get(2).getChildren())
                    .extracting(CommentResponse::getContent)
                    .containsExactly("비밀 댓글입니다.", "공개 대댓글");
        }

        @Test
        void 자신이_쓴_비밀_댓글은_조회할_수_있다() {
            // given
            List<CommentResponse> commentResponses = List.of(
                    AuthCommentResponse.builder()
                            .content("비공개 댓글1")
                            .writer(new WriterResponse(100L, null, null))
                            .secret(true)
                            .build(),
                    AuthCommentResponse.builder()
                            .writer(new WriterResponse(1L, null, null))
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
                                            .writer(new WriterResponse(1L, null, null))
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
                    .processSecret(commentResponses, 1L);

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
