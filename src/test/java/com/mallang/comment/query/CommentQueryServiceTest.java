package com.mallang.comment.query;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.auth.OauthMemberFixture.깃허브_회원;
import static com.mallang.blog.domain.BlogFixture.mallangBlog;
import static com.mallang.comment.CommentFixture.authComment;
import static com.mallang.comment.CommentFixture.unAuthComment;
import static com.mallang.comment.query.response.AuthCommentResponse.WriterResponse.ANONYMOUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.comment.query.repository.CommentQueryRepository;
import com.mallang.comment.query.response.AuthCommentResponse;
import com.mallang.comment.query.response.AuthCommentResponse.WriterResponse;
import com.mallang.comment.query.response.CommentResponse;
import com.mallang.comment.query.response.UnAuthCommentResponse;
import com.mallang.post.PostFixture;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.query.repository.PostQueryRepository;
import java.time.LocalDateTime;
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
class CommentQueryServiceTest {

    private final PostQueryRepository postQueryRepository = mock(PostQueryRepository.class);
    private final MemberQueryRepository memberQueryRepository = mock(MemberQueryRepository.class);
    private final CommentQueryRepository commentQueryRepository = mock(CommentQueryRepository.class);
    private final CommentQueryService commentQueryService = new CommentQueryService(
            postQueryRepository,
            memberQueryRepository,
            commentQueryRepository
    );

    private final Long postId = 1L;
    private final Long mallangId = 1L;
    private final Long donghunId = 2L;
    private final Long otherId = 3L;
    private final Member mallang = 깃허브_말랑(mallangId);
    private final Member donghun = 깃허브_동훈(donghunId);
    private final Member other = 깃허브_회원(otherId, "other");
    private final Blog mallangBlog = mallangBlog(mallang);
    private final Post post = PostFixture.publicPost(postId, mallangBlog);

    private final LocalDateTime ignore = null;
    private final WriterResponse 동훈_작성자_정보 =
            new WriterResponse(donghunId, donghun.getNickname(), donghun.getProfileImageUrl());
    private final WriterResponse other_작성자_정보 =
            new WriterResponse(otherId, other.getNickname(), other.getProfileImageUrl());
    private final UnAuthCommentResponse.WriterResponse 가가_작성자_정보 = new UnAuthCommentResponse.WriterResponse("가가");
    private final UnAuthCommentResponse.WriterResponse 나나_작성자_정보 = new UnAuthCommentResponse.WriterResponse("나나");
    private final UnAuthCommentResponse.WriterResponse 다다_작성자_정보 = new UnAuthCommentResponse.WriterResponse("다다");
    private final UnAuthCommentResponse.WriterResponse 라라_작성자_정보 = new UnAuthCommentResponse.WriterResponse("라라");

    @Nested
    class 특정_포스트의_댓글_모두_조회_시 {

        @BeforeEach
        void setUp() {
            given(memberQueryRepository.getMemberIfIdNotNull(1L)).willReturn(mallang);
            given(memberQueryRepository.getMemberIfIdNotNull(2L)).willReturn(donghun);
            given(memberQueryRepository.getMemberIfIdNotNull(3L)).willReturn(other);
            given(postQueryRepository.getById(1L, mallangBlog.getName()))
                    .willReturn(post);
            var 댓글1 = authComment(1L, "댓글 1", post, null, true, other);
            var 댓글2 = unAuthComment(2L, "댓글 2", post, null, "가가", "1234");
            var 댓글3 = authComment(3L, "댓글 3", post, null, true, donghun);
            var 댓글4 = unAuthComment(4L, "댓글 4", post, null, "나나", "1234");
            var 대댓글1_댓글3 = authComment(5L, "댓글3에 대한 대댓글1", post, 댓글3, true, other);
            var 대댓글2_댓글3 = unAuthComment(6L, "댓글3에 대한 대댓글2", post, 댓글3, "다다", "1234");
            var 대댓글1_댓글4 = authComment(7L, "댓글4에 대한 대댓글1", post, 댓글4, true, donghun);
            var 대댓글2_댓글4 = unAuthComment(8L, "댓글4에 대한 대댓글2", post, 댓글4, "라라", "1234");
            given(commentQueryRepository.findAllByPost(postId, mallangBlog.getName()))
                    .willReturn(List.of(
                            댓글1,
                            댓글2,
                            댓글3,
                            댓글4,
                            대댓글1_댓글3,
                            대댓글2_댓글3,
                            대댓글1_댓글4,
                            대댓글2_댓글4
                    ));
        }

        @Test
        void 인증되지_않은_요청인_경우_비밀_댓글은_비밀_댓글로_조회된다() {
            // when
            List<CommentResponse> result = commentQueryService.findAllByPost(
                    postId,
                    mallangBlog.getName(),
                    null,
                    null
            );

            // then
            var 댓글1_응답 = new AuthCommentResponse(1L, "비밀 댓글입니다.", ignore, false, ANONYMOUS, true);
            var 댓글2_응답 = new UnAuthCommentResponse(2L, "댓글 2", ignore, false, 가가_작성자_정보);
            var 댓글3_응답 = new AuthCommentResponse(3L, "비밀 댓글입니다.", ignore, false, ANONYMOUS, true);
            var 대댓글1_댓글3_응답 = new AuthCommentResponse(5L, "비밀 댓글입니다.", ignore, false, ANONYMOUS, true);
            var 대댓글2_댓글3_응답 = new UnAuthCommentResponse(6L, "댓글3에 대한 대댓글2", ignore, false, 다다_작성자_정보);
            댓글3_응답.setChildren(List.of(대댓글1_댓글3_응답, 대댓글2_댓글3_응답));
            var 댓글4_응답 = new UnAuthCommentResponse(4L, "댓글 4", ignore, false, 나나_작성자_정보);
            var 대댓글1_댓글4_응답 = new AuthCommentResponse(7L, "비밀 댓글입니다.", ignore, false, ANONYMOUS, true);
            var 대댓글2_댓글4_응답 = new UnAuthCommentResponse(8L, "댓글4에 대한 대댓글2", ignore, false, 라라_작성자_정보);
            댓글4_응답.setChildren(List.of(대댓글1_댓글4_응답, 대댓글2_댓글4_응답));
            var expected = List.of(
                    댓글1_응답,
                    댓글2_응답,
                    댓글3_응답,
                    댓글4_응답
            );
            assertThat(result).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
        }

        @Test
        void 내가_쓴_비밀_댓글은_볼_수_있다() {
            // when
            List<CommentResponse> result = commentQueryService.findAllByPost(
                    postId,
                    mallangBlog.getName(),
                    otherId,
                    null
            );

            // then
            var 댓글1_응답 = new AuthCommentResponse(1L, "댓글 1", ignore, false, other_작성자_정보, true);
            var 댓글2_응답 = new UnAuthCommentResponse(2L, "댓글 2", ignore, false, 가가_작성자_정보);
            var 댓글3_응답 = new AuthCommentResponse(3L, "비밀 댓글입니다.", ignore, false, ANONYMOUS, true);
            var 대댓글1_댓글3_응답 = new AuthCommentResponse(5L, "댓글3에 대한 대댓글1", ignore, false, other_작성자_정보, true);
            var 대댓글2_댓글3_응답 = new UnAuthCommentResponse(6L, "댓글3에 대한 대댓글2", ignore, false, 다다_작성자_정보);
            댓글3_응답.setChildren(List.of(대댓글1_댓글3_응답, 대댓글2_댓글3_응답));
            var 댓글4_응답 = new UnAuthCommentResponse(4L, "댓글 4", ignore, false, 나나_작성자_정보);
            var 대댓글1_댓글4_응답 = new AuthCommentResponse(7L, "비밀 댓글입니다.", ignore, false, ANONYMOUS, true);
            var 대댓글2_댓글4_응답 = new UnAuthCommentResponse(8L, "댓글4에 대한 대댓글2", ignore, false, 라라_작성자_정보);
            댓글4_응답.setChildren(List.of(대댓글1_댓글4_응답, 대댓글2_댓글4_응답));
            var expected = List.of(
                    댓글1_응답,
                    댓글2_응답,
                    댓글3_응답,
                    댓글4_응답
            );
            assertThat(result).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
        }

        @Test
        void 내가_쓴_댓글에_달린_비밀_대댓글을_볼_수_있다() {
            // when
            List<CommentResponse> result = commentQueryService.findAllByPost(
                    postId,
                    mallangBlog.getName(),
                    donghunId,
                    null
            );

            // then
            var 댓글1_응답 = new AuthCommentResponse(1L, "비밀 댓글입니다.", ignore, false, ANONYMOUS, true);
            var 댓글2_응답 = new UnAuthCommentResponse(2L, "댓글 2", ignore, false, 가가_작성자_정보);
            var 댓글3_응답 = new AuthCommentResponse(3L, "댓글 3", ignore, false, 동훈_작성자_정보, true);
            var 대댓글1_댓글3_응답 = new AuthCommentResponse(5L, "댓글3에 대한 대댓글1", ignore, false, other_작성자_정보, true);
            var 대댓글2_댓글3_응답 = new UnAuthCommentResponse(6L, "댓글3에 대한 대댓글2", ignore, false, 다다_작성자_정보);
            댓글3_응답.setChildren(List.of(대댓글1_댓글3_응답, 대댓글2_댓글3_응답));
            var 댓글4_응답 = new UnAuthCommentResponse(4L, "댓글 4", ignore, false, 나나_작성자_정보);
            var 대댓글1_댓글4_응답 = new AuthCommentResponse(7L, "댓글4에 대한 대댓글1", ignore, false, 동훈_작성자_정보, true);
            var 대댓글2_댓글4_응답 = new UnAuthCommentResponse(8L, "댓글4에 대한 대댓글2", ignore, false, 라라_작성자_정보);
            댓글4_응답.setChildren(List.of(대댓글1_댓글4_응답, 대댓글2_댓글4_응답));
            var expected = List.of(
                    댓글1_응답,
                    댓글2_응답,
                    댓글3_응답,
                    댓글4_응답
            );
            assertThat(result).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
        }

        @Test
        void 글_작성자는_모든_비밀_댓글을_볼_수_있다() {
            // when
            List<CommentResponse> result = commentQueryService.findAllByPost(
                    postId,
                    mallangBlog.getName(),
                    mallangId,
                    null
            );

            // then
            var 댓글1_응답 = new AuthCommentResponse(1L, "댓글 1", ignore, false, other_작성자_정보, true);
            var 댓글2_응답 = new UnAuthCommentResponse(2L, "댓글 2", ignore, false, 가가_작성자_정보);
            var 댓글3_응답 = new AuthCommentResponse(3L, "댓글 3", ignore, false, 동훈_작성자_정보, true);
            var 대댓글1_댓글3_응답 = new AuthCommentResponse(5L, "댓글3에 대한 대댓글1", ignore, false, other_작성자_정보, true);
            var 대댓글2_댓글3_응답 = new UnAuthCommentResponse(6L, "댓글3에 대한 대댓글2", ignore, false, 다다_작성자_정보);
            댓글3_응답.setChildren(List.of(대댓글1_댓글3_응답, 대댓글2_댓글3_응답));
            var 댓글4_응답 = new UnAuthCommentResponse(4L, "댓글 4", ignore, false, 나나_작성자_정보);
            var 대댓글1_댓글4_응답 = new AuthCommentResponse(7L, "댓글4에 대한 대댓글1", ignore, false, 동훈_작성자_정보, true);
            var 대댓글2_댓글4_응답 = new UnAuthCommentResponse(8L, "댓글4에 대한 대댓글2", ignore, false, 라라_작성자_정보);
            댓글4_응답.setChildren(List.of(대댓글1_댓글4_응답, 대댓글2_댓글4_응답));
            var expected = List.of(
                    댓글1_응답,
                    댓글2_응답,
                    댓글3_응답,
                    댓글4_응답
            );
            assertThat(result).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
        }

        @Test
        void 보호_포스트인_경우_주인이_아니며_비밀번호가_일치하지_않으면_볼_수_없다() {
            // given
            Post post = PostFixture.protectedPost(2L, mallangBlog, "1234");
            given(postQueryRepository.getById(2L, mallangBlog.getName()))
                    .willReturn(post);

            // when & then
            assertThatThrownBy(() -> {
                commentQueryService.findAllByPost(2L, mallangBlog.getName(), donghunId, "wrong");
            }).isInstanceOf(NoAuthorityPostException.class);
            assertDoesNotThrow(() -> {
                commentQueryService.findAllByPost(2L, mallangBlog.getName(), null, "1234");
            });
            assertDoesNotThrow(() -> {
                commentQueryService.findAllByPost(2L, mallangBlog.getName(), mallangId, null);
            });
        }

        @Test
        void 비공개_포스트인_경우_주인이_아니면_볼_수_없다() {
            // given
            Post post = PostFixture.privatePost(2L, mallangBlog);
            given(postQueryRepository.getById(2L, mallangBlog.getName()))
                    .willReturn(post);

            // when & then
            assertThatThrownBy(() -> {
                commentQueryService.findAllByPost(2L, mallangBlog.getName(), donghunId, null);
            }).isInstanceOf(NoAuthorityPostException.class);
            assertThatThrownBy(() -> {
                commentQueryService.findAllByPost(2L, mallangBlog.getName(), null, null);
            }).isInstanceOf(NoAuthorityPostException.class);
            assertDoesNotThrow(() -> {
                commentQueryService.findAllByPost(2L, mallangBlog.getName(), mallangId, null);
            });
        }
    }
}
