package com.mallang.comment.query;

import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.comment.query.response.AuthCommentResponse;
import com.mallang.comment.query.response.CommentResponse;
import com.mallang.comment.query.response.UnAuthCommentResponse;
import com.mallang.common.ServiceTest;
import com.mallang.post.domain.PostId;
import com.mallang.post.exception.NoAuthorityPostException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 조회 서비스 (CommentQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentQueryServiceTest extends ServiceTest {

    private Long memberId;
    private String blogName;
    private PostId postId;

    @Nested
    class 특정_포스트의_댓글_모두_조회_시 {

        @BeforeEach
        void setUp() {
            memberId = 회원을_저장한다("말랑");
            blogName = 블로그_개설(memberId, "mallang");
            postId = 포스트를_저장한다(memberId, blogName, "포스트", "내용");
        }

        @Test
        void 인증되지_않은_요청인_경우_비밀_댓글은_비밀_댓글로_조회된다() {
            // given
            댓글을_작성한다(postId.getPostId(), blogName, "댓글1", false, memberId);
            댓글을_작성한다(postId.getPostId(), blogName, "[비밀] 댓글2", true, memberId);
            비인증_댓글을_작성한다(postId.getPostId(), blogName, "댓글3", "랑말", "1234");

            // when
            List<CommentResponse> result = commentQueryService.findAllByPost(postId.getPostId(), blogName, null, null);

            // then
            assertThat(result)
                    .extracting(CommentResponse::getContent)
                    .containsExactly("댓글1", "비밀 댓글입니다.", "댓글3");
            assertThat(result)
                    .filteredOn(it -> it instanceof AuthCommentResponse)
                    .map(it -> (AuthCommentResponse) it)
                    .extracting(it -> it.getWriter().nickname())
                    .containsExactly("말랑", "익명");
            assertThat(result)
                    .filteredOn(it -> it instanceof UnAuthCommentResponse)
                    .map(it -> (UnAuthCommentResponse) it)
                    .extracting(it -> it.getWriter().nickname())
                    .containsExactly("랑말");
        }

        @Test
        void 내가_쓴_비밀_댓글만_보여지게_조회() {
            // given
            Long dong = 회원을_저장한다("동훈");
            Long hehe = 회원을_저장한다("헤헤");
            댓글을_작성한다(postId.getPostId(), blogName, "동훈 댓글", false, dong);
            댓글을_작성한다(postId.getPostId(), blogName, "헤헤 댓글", false, hehe);
            댓글을_작성한다(postId.getPostId(), blogName, "[비밀] 동훈 댓글2", true, dong); // 제외
            댓글을_작성한다(postId.getPostId(), blogName, "[비밀] 헤헤 댓글2", true, hehe);
            비인증_댓글을_작성한다(postId.getPostId(), blogName, "댓글3", "랑말", "1234");

            // when
            List<CommentResponse> result = commentQueryService.findAllByPost(postId.getPostId(), blogName, dong, null);

            // then
            assertThat(result)
                    .extracting(CommentResponse::getContent)
                    .containsExactly("동훈 댓글", "헤헤 댓글", "[비밀] 동훈 댓글2", "비밀 댓글입니다.", "댓글3");
        }

        @Test
        void 글_작성자는_모든_비밀_댓글을_볼_수_있다() {
            // given
            Long dong = 회원을_저장한다("동훈");
            Long hehe = 회원을_저장한다("헤헤");
            댓글을_작성한다(postId.getPostId(), blogName, "동훈 댓글", false, memberId);
            댓글을_작성한다(postId.getPostId(), blogName, "헤헤 댓글", false, hehe);
            댓글을_작성한다(postId.getPostId(), blogName, "[비밀] 동훈 댓글2", true, memberId); // 제외
            댓글을_작성한다(postId.getPostId(), blogName, "[비밀] 헤헤 댓글2", true, hehe);
            비인증_댓글을_작성한다(postId.getPostId(), blogName, "댓글3", "랑말", "1234");

            // when
            List<CommentResponse> result = commentQueryService.findAllByPost(postId.getPostId(), blogName, memberId,
                    null);

            // then
            assertThat(result)
                    .extracting(CommentResponse::getContent)
                    .containsExactly("동훈 댓글", "헤헤 댓글", "[비밀] 동훈 댓글2", "[비밀] 헤헤 댓글2", "댓글3");
        }

        @Test
        void 보호_포스트인_경우_주인이_아니며_비밀번호가_일치하지_않으면_볼_수_없다() {
            // given
            Long donghunId = 회원을_저장한다("동훈");
            PostId protectedPost = 포스트를_저장한다(memberId, blogName, "title", "content", PROTECTED, "1234", null);

            // when & then
            assertThatThrownBy(() -> {
                commentQueryService.findAllByPost(protectedPost.getPostId(), blogName, donghunId, null);
            }).isInstanceOf(NoAuthorityPostException.class);
            assertDoesNotThrow(() -> {
                commentQueryService.findAllByPost(protectedPost.getPostId(), blogName, null, "1234");
            });
            assertDoesNotThrow(() -> {
                commentQueryService.findAllByPost(protectedPost.getPostId(), blogName, memberId, null);
            });
        }

        @Test
        void 비공개_포스트인_경우_주인이_아니면_볼_수_없다() {
            // given
            Long donghunId = 회원을_저장한다("동훈");
            PostId protectedPost = 포스트를_저장한다(memberId, blogName, "title", "content", PRIVATE, null, null);

            // when & then
            assertThatThrownBy(() -> {
                commentQueryService.findAllByPost(protectedPost.getPostId(), blogName, donghunId, null);
            }).isInstanceOf(NoAuthorityPostException.class);
            assertThatThrownBy(() -> {
                commentQueryService.findAllByPost(protectedPost.getPostId(), blogName, null, null);
            }).isInstanceOf(NoAuthorityPostException.class);
            assertDoesNotThrow(() -> {
                commentQueryService.findAllByPost(protectedPost.getPostId(), blogName, memberId, null);
            });
        }
    }
}
