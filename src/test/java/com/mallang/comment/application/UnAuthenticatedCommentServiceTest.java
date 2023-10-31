package com.mallang.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.blog.domain.BlogName;
import com.mallang.comment.application.command.DeleteAuthenticatedCommentCommand;
import com.mallang.comment.application.command.DeleteUnAuthenticatedCommentCommand;
import com.mallang.comment.application.command.UpdateAuthenticatedCommentCommand;
import com.mallang.comment.application.command.UpdateUnAuthenticatedCommentCommand;
import com.mallang.comment.application.command.WriteAuthenticatedCommentCommand;
import com.mallang.comment.application.command.WriteUnAuthenticatedCommentCommand;
import com.mallang.comment.domain.AuthenticatedComment;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.exception.CommentDepthConstraintViolationException;
import com.mallang.comment.exception.DifferentPostFromParentCommentException;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.comment.exception.NotFoundCommentException;
import com.mallang.common.TransactionHelper;
import com.mallang.member.MemberServiceTestHelper;
import com.mallang.post.application.PostServiceTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DisplayName("댓글 서비스(CommentService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class UnAuthenticatedCommentServiceTest {

    private Long memberId;
    private BlogName blogName;

    @DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
    @SpringBootTest
    @Nested
    class 댓글_작성_시 {

        @Autowired
        private MemberServiceTestHelper memberServiceTestHelper;

        @Autowired
        private BlogServiceTestHelper blogServiceTestHelper;

        @Autowired
        private PostServiceTestHelper postServiceTestHelper;

        @Autowired
        private CommentServiceTestHelper commentServiceTestHelper;

        @Autowired
        private AuthenticatedCommentService authenticatedCommentService;

        @Autowired
        private UnAuthenticatedCommentService unAuthenticatedCommentService;

        @Autowired
        private TransactionHelper transactionHelper;


        @BeforeEach
        void setUp() {
            memberId = memberServiceTestHelper.회원을_저장한다("말랑");
            blogName = blogServiceTestHelper.블로그_개설(memberId, "mallang");
        }

        @Test
        void 로그인한_사용자가_댓글을_작성한다() {
            // given
            Long 댓글작성자_ID = memberServiceTestHelper.회원을_저장한다("댓글작성자");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용");
            WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                    .postId(포스트_ID)
                    .content("댓글입니다.")
                    .memberId(댓글작성자_ID)
                    .secret(false)
                    .build();

            // when
            Long 댓글_ID = authenticatedCommentService.write(command);

            // then
            assertThat(댓글_ID).isNotNull();
        }

        @Test
        void 로그인한_사용자는_비밀_댓글_작성이_가능하다() {
            // given
            Long 댓글작성자_ID = memberServiceTestHelper.회원을_저장한다("댓글작성자");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용");
            WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                    .postId(포스트_ID)
                    .content("댓글입니다.")
                    .memberId(댓글작성자_ID)
                    .secret(true)
                    .build();

            // when
            Long 댓글_ID = authenticatedCommentService.write(command);

            // then
            assertThat(댓글_ID).isNotNull();
        }

        @Test
        void 로그인하지_않은_사용자도_댓글을_달_수_있다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용");
            WriteUnAuthenticatedCommentCommand command = WriteUnAuthenticatedCommentCommand.builder()
                    .postId(포스트_ID)
                    .content("댓글입니다.")
                    .nickname("비인증1")
                    .password("1234")
                    .build();

            // when
            Long 댓글_ID = unAuthenticatedCommentService.write(command);

            // then
            assertThat(댓글_ID).isNotNull();
        }

        @Test
        void 대댓글을_작성할_수_있다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용");
            Long 말랑_댓글_ID = commentServiceTestHelper.댓글을_작성한다(포스트_ID, "말랑 댓글", false, memberId);
            WriteUnAuthenticatedCommentCommand command = WriteUnAuthenticatedCommentCommand.builder()
                    .postId(포스트_ID)
                    .content("대댓글입니다.")
                    .nickname("비인증1")
                    .password("1234")
                    .parentCommentId(말랑_댓글_ID)
                    .build();

            // when
            Long 대댓글_ID = unAuthenticatedCommentService.write(command);

            // then
            transactionHelper.doAssert(() -> {
                assertThat(대댓글_ID).isNotNull();
                Comment 대댓글 = commentServiceTestHelper.비인증_댓글을_조회한다(대댓글_ID);
                Comment 말랑_댓글 = commentServiceTestHelper.인증된_댓글을_조회한다(말랑_댓글_ID);
                assertThat(대댓글.getParent()).isEqualTo(말랑_댓글);
                assertThat(말랑_댓글.getChildren().get(0)).isEqualTo(대댓글);
            });
        }

        @Test
        void 다른_사람의_댓글에_대댓글을_달_수_있다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용");
            Long 말랑_댓글_ID = commentServiceTestHelper.댓글을_작성한다(포스트_ID, "말랑 댓글", true, memberId);
            Long 동훈_ID = memberServiceTestHelper.회원을_저장한다("동훈");
            WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                    .postId(포스트_ID)
                    .content("대댓글입니다.")
                    .memberId(동훈_ID)
                    .parentCommentId(말랑_댓글_ID)
                    .build();

            // when
            Long 대댓글_ID = authenticatedCommentService.write(command);

            // then
            transactionHelper.doAssert(() -> {
                assertThat(대댓글_ID).isNotNull();
                Comment 대댓글 = commentServiceTestHelper.인증된_댓글을_조회한다(대댓글_ID);
                Comment 말랑_댓글 = commentServiceTestHelper.인증된_댓글을_조회한다(말랑_댓글_ID);
                assertThat(대댓글.getParent()).isEqualTo(말랑_댓글);
                assertThat(말랑_댓글.getChildren().get(0)).isEqualTo(대댓글);
            });
        }

        @Test
        void 대댓글에_대해서는_댓글을_달_수_없다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용");
            Long 말랑_댓글_ID = commentServiceTestHelper.댓글을_작성한다(포스트_ID, "말랑 댓글", true, memberId);
            Long 대댓글_ID = commentServiceTestHelper.대댓글을_작성한다(포스트_ID, "대댓글", false, memberId, 말랑_댓글_ID);
            WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                    .postId(포스트_ID)
                    .content("대댓글입니다.")
                    .memberId(memberId)
                    .parentCommentId(대댓글_ID)
                    .build();

            // when
            assertThatThrownBy(() ->
                    authenticatedCommentService.write(command)
            ).isInstanceOf(CommentDepthConstraintViolationException.class);

            // then
            transactionHelper.doAssert(() -> {
                Comment 대댓글 = commentServiceTestHelper.인증된_댓글을_조회한다(대댓글_ID);
                assertThat(대댓글.getChildren()).isEmpty();
            });
        }

        @Test
        void 대댓글을_다는_경우_부모_댓글과_Post_가_다르면_예외이다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용");
            Long 포스트2_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트2", "내용");
            Long 말랑_댓글_ID = commentServiceTestHelper.댓글을_작성한다(포스트_ID, "말랑 댓글", true, memberId);
            WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                    .postId(포스트2_ID)
                    .content("대댓글입니다.")
                    .memberId(memberId)
                    .parentCommentId(말랑_댓글_ID)
                    .build();

            // when
            assertThatThrownBy(() ->
                    authenticatedCommentService.write(command)
            ).isInstanceOf(DifferentPostFromParentCommentException.class);

            // then
            transactionHelper.doAssert(() -> {
                Comment 대댓글 = commentServiceTestHelper.인증된_댓글을_조회한다(말랑_댓글_ID);
                assertThat(대댓글.getChildren()).isEmpty();
            });
        }
    }

    @DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
    @SpringBootTest
    @Nested
    class 댓글_수정_시 {

        @Autowired
        private BlogServiceTestHelper blogServiceTestHelper;

        @Autowired
        private MemberServiceTestHelper memberServiceTestHelper;

        @Autowired
        private PostServiceTestHelper postServiceTestHelper;

        @Autowired
        private CommentServiceTestHelper commentServiceTestHelper;

        @Autowired
        private AuthenticatedCommentService authenticatedCommentService;

        @Autowired
        private UnAuthenticatedCommentService unAuthenticatedCommentService;

        private Long postWriterId;
        private Long postId;

        @BeforeEach
        void setUp() {
            postWriterId = memberServiceTestHelper.회원을_저장한다("말랑");
            BlogName postWriterBlogName = blogServiceTestHelper.블로그_개설(postWriterId, "postwriter");
            postId = postServiceTestHelper.포스트를_저장한다(postWriterId, postWriterBlogName, "제목", "내용");
            memberId = memberServiceTestHelper.회원을_저장한다("닝냥");
            blogName = blogServiceTestHelper.블로그_개설(memberId, "mallang");
        }

        @Test
        void 댓글이_수정된다() {
            // given
            Long commentId = commentServiceTestHelper.댓글을_작성한다(postId, "댓글", false, memberId);
            UpdateAuthenticatedCommentCommand command = UpdateAuthenticatedCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .secret(false)
                    .memberId(memberId)
                    .build();

            // when
            authenticatedCommentService.update(command);

            // then
            Comment find = commentServiceTestHelper.인증된_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("수정");
        }

        @Test
        void 인증된_사용자의_경우_비공개_여부도_수정할_수_있다() {
            // given
            Long commentId = commentServiceTestHelper.댓글을_작성한다(postId, "댓글", false, memberId);
            UpdateAuthenticatedCommentCommand command = UpdateAuthenticatedCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .secret(true)
                    .memberId(memberId)
                    .build();

            // when
            authenticatedCommentService.update(command);

            // then
            AuthenticatedComment find = commentServiceTestHelper.인증된_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("수정");
            assertThat(find.isSecret()).isTrue();
        }

        @Test
        void 자신의_댓글이_아닌_경우_오류이다() {
            // given
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("otherMember");
            Long commentId = commentServiceTestHelper.댓글을_작성한다(postId, "댓글", false, memberId);
            UpdateAuthenticatedCommentCommand command = UpdateAuthenticatedCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .secret(true)
                    .memberId(otherMemberId)
                    .build();

            // when
            assertThatThrownBy(() ->
                    authenticatedCommentService.update(command)
            ).isInstanceOf(NoAuthorityForCommentException.class);

            // then
            AuthenticatedComment find = commentServiceTestHelper.인증된_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("댓글");
            assertThat(find.isSecret()).isFalse();
        }

        @Test
        void 비인증_댓글은_비밀번호가_일치하면_수정할_수_있다() {
            // given
            Long commentId = commentServiceTestHelper.비인증_댓글을_작성한다(postId, "댓글", "mal", "1234");
            UpdateUnAuthenticatedCommentCommand command = UpdateUnAuthenticatedCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .password("1234")
                    .build();

            // when
            unAuthenticatedCommentService.update(command);

            // then
            Comment find = commentServiceTestHelper.비인증_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("수정");
        }

        @Test
        void 비인증_댓글_수정_시_비밀번호가_틀리면_오류이다() {
            // given
            Long commentId = commentServiceTestHelper.비인증_댓글을_작성한다(postId, "댓글", "mal", "1234");
            UpdateUnAuthenticatedCommentCommand command = UpdateUnAuthenticatedCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .password("12345")
                    .build();

            // when
            assertThatThrownBy(() ->
                    unAuthenticatedCommentService.update(command)
            ).isInstanceOf(NoAuthorityForCommentException.class);

            // then
            Comment find = commentServiceTestHelper.비인증_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("댓글");
        }

        @Test
        void 포스트_주인도_댓글을_수정할수는_없다() {
            // given
            Long commentId = commentServiceTestHelper.댓글을_작성한다(postId, "댓글", false, memberId);
            UpdateAuthenticatedCommentCommand command = UpdateAuthenticatedCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .secret(false)
                    .memberId(postWriterId)
                    .build();

            // when
            assertThatThrownBy(() ->
                    authenticatedCommentService.update(command)
            ).isInstanceOf(NoAuthorityForCommentException.class);

            // then
            Comment find = commentServiceTestHelper.인증된_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("댓글");
        }
    }

    @DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
    @SpringBootTest
    @Nested
    class 댓글_제거_시 {

        @Autowired
        private BlogServiceTestHelper blogServiceTestHelper;

        @Autowired
        private MemberServiceTestHelper memberServiceTestHelper;

        @Autowired
        private PostServiceTestHelper postServiceTestHelper;

        @Autowired
        private CommentServiceTestHelper commentServiceTestHelper;

        @Autowired
        private AuthenticatedCommentService authenticatedCommentService;

        @Autowired
        private UnAuthenticatedCommentService unAuthenticatedCommentService;

        @Autowired
        private TransactionHelper transactionHelper;

        private Long postWriterId;
        private Long postId;

        @BeforeEach
        void setUp() {
            postWriterId = memberServiceTestHelper.회원을_저장한다("말랑");
            BlogName postWriterBlogName = blogServiceTestHelper.블로그_개설(postWriterId, "postwriter");
            postId = postServiceTestHelper.포스트를_저장한다(postWriterId, postWriterBlogName, "제목", "내용");
            memberId = memberServiceTestHelper.회원을_저장한다("닝냥");
            blogName = blogServiceTestHelper.블로그_개설(memberId, "mallang");
        }

        @Test
        void 댓글_작성자는_자신의_댓글을_제거할_수_있다() {
            // given
            Long commentId = commentServiceTestHelper.댓글을_작성한다(postId, "댓글", false, memberId);
            DeleteAuthenticatedCommentCommand command = DeleteAuthenticatedCommentCommand.builder()
                    .commentId(commentId)
                    .memberId(memberId)
                    .build();

            // when
            authenticatedCommentService.delete(command);

            // then
            assertThatThrownBy(() ->
                    commentServiceTestHelper.인증된_댓글을_조회한다(commentId)
            ).isInstanceOf(NotFoundCommentException.class);
        }

        @Test
        void 자신의_댓글이_아닌_경우_오류() {
            // given
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("otherMember");
            Long commentId = commentServiceTestHelper.댓글을_작성한다(postId, "댓글", false, memberId);
            DeleteAuthenticatedCommentCommand command = DeleteAuthenticatedCommentCommand.builder()
                    .commentId(commentId)
                    .memberId(otherMemberId)
                    .build();

            // when
            assertThatThrownBy(() ->
                    authenticatedCommentService.delete(command)
            ).isInstanceOf(NoAuthorityForCommentException.class);

            // then
            Comment find = commentServiceTestHelper.인증된_댓글을_조회한다(commentId);
            assertThat(find).isNotNull();
        }

        @Test
        void 비인증_댓글은_비밀번호가_일치하면_제거할_수_있다() {
            // given
            Long commentId = commentServiceTestHelper.비인증_댓글을_작성한다(postId, "댓글", "mal", "1234");
            DeleteUnAuthenticatedCommentCommand command = DeleteUnAuthenticatedCommentCommand.builder()
                    .commentId(commentId)
                    .password("1234")
                    .build();

            // when
            unAuthenticatedCommentService.delete(command);

            // then
            assertThatThrownBy(() ->
                    commentServiceTestHelper.비인증_댓글을_조회한다(commentId)
            ).isInstanceOf(NotFoundCommentException.class);
        }

        @Test
        void 비인증_댓글은_비밀번호가_일치하지_않다면_제거할_수_없다() {
            // given
            Long commentId = commentServiceTestHelper.비인증_댓글을_작성한다(postId, "댓글", "mal", "1234");
            DeleteUnAuthenticatedCommentCommand command = DeleteUnAuthenticatedCommentCommand.builder()
                    .commentId(commentId)
                    .password("12345")
                    .build();

            // when
            assertThatThrownBy(() ->
                    unAuthenticatedCommentService.delete(command)
            ).isInstanceOf(NoAuthorityForCommentException.class);

            // then
            Comment find = commentServiceTestHelper.비인증_댓글을_조회한다(commentId);
            assertThat(find).isNotNull();
        }

        @Test
        void 포스트_작성자는_모든_댓글을_제거할_수_있다() {
            // given
            Long comment1Id = commentServiceTestHelper.댓글을_작성한다(postId, "댓글", false, memberId);
            Long comment2Id = commentServiceTestHelper.비인증_댓글을_작성한다(postId, "댓글", "mal", "1234");
            DeleteAuthenticatedCommentCommand command1 = DeleteAuthenticatedCommentCommand.builder()
                    .commentId(comment1Id)
                    .memberId(postWriterId)
                    .build();
            DeleteUnAuthenticatedCommentCommand command2 = DeleteUnAuthenticatedCommentCommand.builder()
                    .commentId(comment2Id)
                    .memberId(postWriterId)
                    .build();

            // when
            authenticatedCommentService.delete(command1);
            unAuthenticatedCommentService.delete(command2);

            // then
            assertThatThrownBy(() ->
                    commentServiceTestHelper.인증된_댓글을_조회한다(comment1Id)
            ).isInstanceOf(NotFoundCommentException.class);
            assertThatThrownBy(() ->
                    commentServiceTestHelper.인증된_댓글을_조회한다(comment2Id)
            ).isInstanceOf(NotFoundCommentException.class);
        }

        @Test
        void 대댓글_제거_시_부모_댓글과의_관계도_끊어진다() {
            // given
            Long 말랑_댓글_ID = commentServiceTestHelper.댓글을_작성한다(postId, "말랑 댓글", false, memberId);
            Long 대댓글_ID = commentServiceTestHelper.비인증_대댓글을_작성한다(postId, "대댓글", "hi", "12", 말랑_댓글_ID);
            DeleteUnAuthenticatedCommentCommand command = DeleteUnAuthenticatedCommentCommand.builder()
                    .commentId(대댓글_ID)
                    .password("12")
                    .build();

            // when
            unAuthenticatedCommentService.delete(command);

            // then
            assertThatThrownBy(() ->
                    commentServiceTestHelper.비인증_댓글을_조회한다(대댓글_ID)
            ).isInstanceOf(NotFoundCommentException.class);
            transactionHelper.doAssert(() -> {
                Comment 말랑_댓글 = commentServiceTestHelper.인증된_댓글을_조회한다(말랑_댓글_ID);
                assertThat(말랑_댓글.getChildren()).isEmpty();
            });
        }

        @Test
        void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태가_아닌_경우_부모는_변함없다() {
            // given
            Long 댓글_ID = commentServiceTestHelper.비인증_댓글을_작성한다(postId, "말랑 댓글", "hi", "1");
            Long 대댓글_ID = commentServiceTestHelper.비인증_댓글을_작성한다(postId, "대댓글", "hi2", "12");
            DeleteUnAuthenticatedCommentCommand command = DeleteUnAuthenticatedCommentCommand.builder()
                    .commentId(대댓글_ID)
                    .password("12")
                    .build();

            // when
            unAuthenticatedCommentService.delete(command);

            // then
            assertThatThrownBy(() ->
                    commentServiceTestHelper.비인증_댓글을_조회한다(대댓글_ID)
            ).isInstanceOf(NotFoundCommentException.class);
            transactionHelper.doAssert(() -> {
                Comment 댓글 = commentServiceTestHelper.비인증_댓글을_조회한다(댓글_ID);
                assertThat(댓글.isDeleted()).isFalse();
            });
        }

        @Test
        void 댓글_제거_시_자식_댓글이_존재한다면_부모와의_연관관계는_유지되며_논리적으로만_제거시킨다() {
            // given
            Long 댓글_ID = commentServiceTestHelper.비인증_댓글을_작성한다(postId, "말랑 댓글", "hi", "1234");
            Long 대댓글_ID = commentServiceTestHelper.대댓글을_작성한다(postId, "대댓글", false, memberId, 댓글_ID);
            DeleteUnAuthenticatedCommentCommand command = DeleteUnAuthenticatedCommentCommand.builder()
                    .commentId(댓글_ID)
                    .password("1234")
                    .build();

            // when
            unAuthenticatedCommentService.delete(command);

            // then
            transactionHelper.doAssert(() -> {
                Comment 대댓글 = commentServiceTestHelper.인증된_댓글을_조회한다(대댓글_ID);
                Comment 제거된_말랑_댓글 = commentServiceTestHelper.비인증_댓글을_조회한다(댓글_ID);
                assertThat(대댓글.getParent()).isEqualTo(제거된_말랑_댓글);
                assertThat(대댓글.isDeleted()).isFalse();
                assertThat(제거된_말랑_댓글.isDeleted()).isTrue();
                assertThat(제거된_말랑_댓글.getChildren().get(0)).isEqualTo(대댓글);
            });
        }

        @Test
        void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태이며_더이상_존재하는_자식이_없는_경우_부모_댓글도_물리적으로_제거된다() {
            // given
            Long 댓글_ID = commentServiceTestHelper.비인증_댓글을_작성한다(postId, "말랑 댓글", "hi", "hi");
            Long 대댓글_ID = commentServiceTestHelper.비인증_대댓글을_작성한다(postId, "대댓글", "hi2", "12", 댓글_ID);
            commentServiceTestHelper.비인증_댓글을_제거한다(댓글_ID, "hi");

            DeleteUnAuthenticatedCommentCommand command = DeleteUnAuthenticatedCommentCommand.builder()
                    .commentId(대댓글_ID)
                    .password("12")
                    .build();

            // when
            unAuthenticatedCommentService.delete(command);

            // then
            assertThatThrownBy(() ->
                    commentServiceTestHelper.비인증_댓글을_조회한다(댓글_ID)
            ).isInstanceOf(NotFoundCommentException.class);
            assertThatThrownBy(() ->
                    commentServiceTestHelper.비인증_댓글을_조회한다(대댓글_ID)
            ).isInstanceOf(NotFoundCommentException.class);
        }
    }
}
