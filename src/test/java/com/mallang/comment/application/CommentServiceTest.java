package com.mallang.comment.application;

import static com.mallang.common.EventsTestUtils.count;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.comment.application.command.DeleteAuthCommentCommand;
import com.mallang.comment.application.command.DeleteUnAuthCommentCommand;
import com.mallang.comment.application.command.UpdateAuthCommentCommand;
import com.mallang.comment.application.command.UpdateUnAuthCommentCommand;
import com.mallang.comment.application.command.WriteAuthCommentCommand;
import com.mallang.comment.application.command.WriteUnAuthCommentCommand;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentWrittenEvent;
import com.mallang.comment.domain.UnAuthComment;
import com.mallang.comment.exception.CommentDepthConstraintViolationException;
import com.mallang.comment.exception.NoAuthorityCommentException;
import com.mallang.comment.exception.NotFoundCommentException;
import com.mallang.common.ServiceTest;
import com.mallang.post.domain.PostId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 서비스 (CommentService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentServiceTest extends ServiceTest {

    private Long postWriterId;
    private Long other1Id;
    private Long other2Id;
    private String blogName;
    private PostId postId;

    @BeforeEach
    void setUp() {
        postWriterId = 회원을_저장한다("말랑");
        other1Id = 회원을_저장한다("other1");
        other2Id = 회원을_저장한다("other2");
        blogName = 블로그_개설(postWriterId, "mallang");
        postId = 포스트를_저장한다(postWriterId, blogName, "포스트", "내용");
    }

    @Nested
    class 댓글_작성_시 {

        @Test
        void 로그인한_사용자가_댓글을_작성한다() {
            // given
            WriteAuthCommentCommand command = WriteAuthCommentCommand.builder()
                    .postId(postId.getPostId())
                    .blogName(blogName)
                    .content("댓글입니다.")
                    .memberId(other1Id)
                    .secret(false)
                    .build();

            // when
            Long 댓글_ID = authCommentService.write(command);

            // then
            assertThat(댓글_ID).isNotNull();
            assertThat(count(events, CommentWrittenEvent.class)).isEqualTo(1);
        }

        @Test
        void 로그인한_사용자는_비밀_댓글_작성이_가능하다() {
            // given
            WriteAuthCommentCommand command = WriteAuthCommentCommand.builder()
                    .postId(postId.getPostId())
                    .blogName(blogName)
                    .content("댓글입니다.")
                    .memberId(other1Id)
                    .secret(true)
                    .build();

            // when
            Long 댓글_ID = authCommentService.write(command);

            // then
            assertThat(댓글_ID).isNotNull();
            assertThat(count(events, CommentWrittenEvent.class)).isEqualTo(1);
        }

        @Test
        void 로그인하지_않은_사용자도_댓글을_달_수_있다() {
            // given
            WriteUnAuthCommentCommand command = WriteUnAuthCommentCommand.builder()
                    .postId(postId.getPostId())
                    .blogName(blogName)
                    .content("댓글입니다.")
                    .nickname("비인증1")
                    .password("1234")
                    .build();

            // when
            Long 댓글_ID = unAuthCommentService.write(command);

            // then
            assertThat(댓글_ID).isNotNull();
            assertThat(count(events, CommentWrittenEvent.class)).isEqualTo(1);
        }

        @Test
        void 대댓글을_작성할_수_있다() {
            // given
            Long 말랑_댓글_ID = 댓글을_작성한다(postId.getPostId(), blogName, "말랑 댓글", false, postWriterId);
            events.clear();
            WriteUnAuthCommentCommand command = WriteUnAuthCommentCommand.builder()
                    .postId(postId.getPostId())
                    .blogName(blogName)
                    .content("대댓글입니다.")
                    .nickname("비인증1")
                    .password("1234")
                    .parentCommentId(말랑_댓글_ID)
                    .build();

            // when
            Long 대댓글_ID = unAuthCommentService.write(command);

            // then
            transactionHelper.doAssert(() -> {
                assertThat(대댓글_ID).isNotNull();
                Comment 대댓글 = 비인증_댓글을_조회한다(대댓글_ID);
                Comment 말랑_댓글 = 인증된_댓글을_조회한다(말랑_댓글_ID);
                assertThat(대댓글.getParent()).isEqualTo(말랑_댓글);
                assertThat(말랑_댓글.getChildren().get(0)).isEqualTo(대댓글);
            });
            assertThat(count(events, CommentWrittenEvent.class)).isEqualTo(1);
        }

        @Test
        void 다른_사람의_댓글에_대댓글을_달_수_있다() {
            // given
            Long 말랑_댓글_ID = 댓글을_작성한다(postId.getPostId(), blogName, "말랑 댓글", true, postWriterId);
            events.clear();
            WriteAuthCommentCommand command = WriteAuthCommentCommand.builder()
                    .postId(postId.getPostId())
                    .blogName(blogName)
                    .content("대댓글입니다.")
                    .memberId(other1Id)
                    .parentCommentId(말랑_댓글_ID)
                    .build();

            // when
            Long 대댓글_ID = authCommentService.write(command);

            // then
            transactionHelper.doAssert(() -> {
                assertThat(대댓글_ID).isNotNull();
                Comment 대댓글 = 인증된_댓글을_조회한다(대댓글_ID);
                Comment 말랑_댓글 = 인증된_댓글을_조회한다(말랑_댓글_ID);
                assertThat(대댓글.getParent()).isEqualTo(말랑_댓글);
                assertThat(말랑_댓글.getChildren().get(0)).isEqualTo(대댓글);
            });
            assertThat(count(events, CommentWrittenEvent.class)).isEqualTo(1);
        }

        @Test
        void 대댓글에_대해서는_댓글을_달_수_없다() {
            // given
            Long 말랑_댓글_ID = 댓글을_작성한다(postId.getPostId(), blogName, "말랑 댓글", true, postWriterId);
            Long 대댓글_ID = 대댓글을_작성한다(postId.getPostId(), blogName, "대댓글", false, postWriterId, 말랑_댓글_ID);
            events.clear();
            WriteAuthCommentCommand command = WriteAuthCommentCommand.builder()
                    .postId(postId.getPostId())
                    .blogName(blogName)
                    .content("대댓글입니다.")
                    .memberId(postWriterId)
                    .parentCommentId(대댓글_ID)
                    .build();

            // when
            assertThatThrownBy(() ->
                    authCommentService.write(command)
            ).isInstanceOf(CommentDepthConstraintViolationException.class);

            // then
            transactionHelper.doAssert(() -> {
                Comment 대댓글 = 인증된_댓글을_조회한다(대댓글_ID);
                assertThat(대댓글.getChildren()).isEmpty();
            });
            assertThat(count(events, CommentWrittenEvent.class)).isZero();
        }

        @Test
        void 대댓글을_다는_경우_부모_댓글과_Post_가_다르면_예외() {
            // given
            PostId 포스트2_ID = 포스트를_저장한다(postWriterId, blogName, "포스트2", "내용");
            Long 말랑_댓글_ID = 댓글을_작성한다(postId.getPostId(), blogName, "말랑 댓글", true, postWriterId);
            events.clear();
            WriteAuthCommentCommand command = WriteAuthCommentCommand.builder()
                    .postId(포스트2_ID.getPostId())
                    .blogName(blogName)
                    .content("대댓글입니다.")
                    .memberId(postWriterId)
                    .parentCommentId(말랑_댓글_ID)
                    .build();

            // when
            assertThatThrownBy(() ->
                    authCommentService.write(command)
            ).isInstanceOf(NotFoundCommentException.class);

            // then
            transactionHelper.doAssert(() -> {
                Comment 대댓글 = 인증된_댓글을_조회한다(말랑_댓글_ID);
                assertThat(대댓글.getChildren()).isEmpty();
            });
            assertThat(count(events, CommentWrittenEvent.class)).isZero();
        }
    }

    @Nested
    class 댓글_수정_시 {

        @Test
        void 댓글이_수정된다() {
            // given
            Long commentId = 댓글을_작성한다(postId.getPostId(), blogName, "댓글", false, other1Id);
            UpdateAuthCommentCommand command = UpdateAuthCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .secret(false)
                    .memberId(other1Id)
                    .build();

            // when
            authCommentService.update(command);

            // then
            AuthComment find = 인증된_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("수정");
        }

        @Test
        void 인증된_사용자의_경우_비공개_여부도_수정할_수_있다() {
            // given
            Long commentId = 댓글을_작성한다(postId.getPostId(), blogName, "댓글", false, other1Id);
            UpdateAuthCommentCommand command = UpdateAuthCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .secret(true)
                    .memberId(other1Id)
                    .build();

            // when
            authCommentService.update(command);

            // then
            AuthComment find = 인증된_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("수정");
            assertThat(find.isSecret()).isTrue();
        }

        @Test
        void 자신의_댓글이_아닌_경우_예외() {
            // given
            Long commentId = 댓글을_작성한다(postId.getPostId(), blogName, "댓글", false, other1Id);
            UpdateAuthCommentCommand command = UpdateAuthCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .secret(true)
                    .memberId(other2Id)
                    .build();

            // when
            assertThatThrownBy(() ->
                    authCommentService.update(command)
            ).isInstanceOf(NoAuthorityCommentException.class);

            // then
            AuthComment find = 인증된_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("댓글");
            assertThat(find.isSecret()).isFalse();
        }

        @Test
        void 비인증_댓글은_비밀번호가_일치하면_수정할_수_있다() {
            // given
            Long commentId = 비인증_댓글을_작성한다(postId.getPostId(), blogName, "댓글", "mal", "1234");
            UpdateUnAuthCommentCommand command = UpdateUnAuthCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .password("1234")
                    .build();

            // when
            unAuthCommentService.update(command);

            // then
            UnAuthComment find = 비인증_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("수정");
        }

        @Test
        void 비인증_댓글_수정_시_비밀번호가_틀리면_예외() {
            // given
            Long commentId = 비인증_댓글을_작성한다(postId.getPostId(), blogName, "댓글", "mal", "1234");
            UpdateUnAuthCommentCommand command = UpdateUnAuthCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .password("123")
                    .build();

            // when
            assertThatThrownBy(() ->
                    unAuthCommentService.update(command)
            ).isInstanceOf(NoAuthorityCommentException.class);

            // then
            UnAuthComment find = 비인증_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("댓글");
        }

        @Test
        void 포스트_주인도_댓글을_수정할수는_없다() {
            // given
            Long commentId = 댓글을_작성한다(postId.getPostId(), blogName, "댓글", false, other1Id);
            UpdateAuthCommentCommand command = UpdateAuthCommentCommand.builder()
                    .commentId(commentId)
                    .content("수정")
                    .secret(true)
                    .memberId(postWriterId)
                    .build();

            // when
            assertThatThrownBy(() ->
                    authCommentService.update(command)
            ).isInstanceOf(NoAuthorityCommentException.class);

            // then
            AuthComment find = 인증된_댓글을_조회한다(commentId);
            assertThat(find.getContent()).isEqualTo("댓글");
            assertThat(find.isSecret()).isFalse();
        }
    }

    @Nested
    class 댓글_제거_시 {

        @Test
        void 댓글_작성자는_자신의_댓글을_제거할_수_있다() {
            // given
            Long commentId = 댓글을_작성한다(
                    postId.getPostId(), blogName, "댓글", false, postWriterId);
            DeleteAuthCommentCommand command = DeleteAuthCommentCommand.builder()
                    .commentId(commentId)
                    .memberId(postWriterId)
                    .build();

            // when
            authCommentService.delete(command);

            // then
            assertThatThrownBy(() ->
                    인증된_댓글을_조회한다(commentId)
            ).isInstanceOf(NotFoundCommentException.class);
        }

        @Test
        void 자신의_댓글이_아닌_경우_예외() {
            // given
            Long commentId = 댓글을_작성한다(postId.getPostId(), blogName, "댓글", false, postWriterId);
            DeleteAuthCommentCommand command = DeleteAuthCommentCommand.builder()
                    .commentId(commentId)
                    .memberId(other1Id)
                    .build();

            // when
            assertThatThrownBy(() ->
                    authCommentService.delete(command)
            ).isInstanceOf(NoAuthorityCommentException.class);

            // then
            Comment find = 인증된_댓글을_조회한다(commentId);
            assertThat(find).isNotNull();
        }

        @Test
        void 비인증_댓글은_비밀번호가_일치하면_제거할_수_있다() {
            // given
            Long commentId = 비인증_댓글을_작성한다(postId.getPostId(), blogName, "댓글", "mal", "1234");
            DeleteUnAuthCommentCommand command = DeleteUnAuthCommentCommand.builder()
                    .commentId(commentId)
                    .password("1234")
                    .build();

            // when
            unAuthCommentService.delete(command);

            // then
            assertThatThrownBy(() ->
                    비인증_댓글을_조회한다(commentId)
            ).isInstanceOf(NotFoundCommentException.class);
        }

        @Test
        void 비인증_댓글은_비밀번호가_일치하지_않다면_제거할_수_없다() {
            // given
            Long commentId = 비인증_댓글을_작성한다(postId.getPostId(), blogName, "댓글", "mal", "1234");
            DeleteUnAuthCommentCommand command = DeleteUnAuthCommentCommand.builder()
                    .commentId(commentId)
                    .password("12345")
                    .build();

            // when
            assertThatThrownBy(() ->
                    unAuthCommentService.delete(command)
            ).isInstanceOf(NoAuthorityCommentException.class);

            // then
            Comment find = 비인증_댓글을_조회한다(commentId);
            assertThat(find).isNotNull();
        }

        @Test
        void 포스트_작성자는_모든_댓글을_제거할_수_있다() {
            // given
            Long comment1Id = 댓글을_작성한다(postId.getPostId(), blogName, "댓글", false, other1Id);
            Long comment2Id = 비인증_댓글을_작성한다(postId.getPostId(), blogName, "댓글", "mal", "1234");
            DeleteAuthCommentCommand command1 = DeleteAuthCommentCommand.builder()
                    .commentId(comment1Id)
                    .memberId(postWriterId)
                    .build();
            DeleteUnAuthCommentCommand command2 = DeleteUnAuthCommentCommand.builder()
                    .commentId(comment2Id)
                    .memberId(postWriterId)
                    .build();

            // when
            authCommentService.delete(command1);
            unAuthCommentService.delete(command2);

            // then
            assertThatThrownBy(() ->
                    인증된_댓글을_조회한다(comment1Id)
            ).isInstanceOf(NotFoundCommentException.class);
            assertThatThrownBy(() ->
                    비인증_댓글을_조회한다(comment2Id)
            ).isInstanceOf(NotFoundCommentException.class);
        }

        @Test
        void 대댓글_제거_시_부모_댓글과의_관계도_끊어진다() {
            // given
            Long 말랑_댓글_ID = 댓글을_작성한다(postId.getPostId(), blogName, "말랑 댓글", false, postWriterId);
            Long 대댓글_ID = 비인증_대댓글을_작성한다(postId.getPostId(), blogName, "대댓글", "hi", "12", 말랑_댓글_ID);
            DeleteUnAuthCommentCommand command = DeleteUnAuthCommentCommand.builder()
                    .commentId(대댓글_ID)
                    .password("12")
                    .build();

            // when
            unAuthCommentService.delete(command);

            // then
            assertThatThrownBy(() ->
                    비인증_댓글을_조회한다(대댓글_ID)
            ).isInstanceOf(NotFoundCommentException.class);
            transactionHelper.doAssert(() -> {
                Comment 말랑_댓글 = 인증된_댓글을_조회한다(말랑_댓글_ID);
                assertThat(말랑_댓글.getChildren()).isEmpty();
            });
        }

        @Test
        void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태가_아닌_경우_부모는_변함없다() {
            // given
            Long 댓글_ID = 비인증_댓글을_작성한다(postId.getPostId(), blogName, "말랑 댓글", "hi", "1");
            Long 대댓글_ID = 비인증_댓글을_작성한다(postId.getPostId(), blogName, "대댓글", "hi2", "12");
            DeleteUnAuthCommentCommand command = DeleteUnAuthCommentCommand.builder()
                    .commentId(대댓글_ID)
                    .password("12")
                    .build();

            // when
            unAuthCommentService.delete(command);

            // then
            assertThatThrownBy(() ->
                    비인증_댓글을_조회한다(대댓글_ID)
            ).isInstanceOf(NotFoundCommentException.class);
            transactionHelper.doAssert(() -> {
                Comment 댓글 = 비인증_댓글을_조회한다(댓글_ID);
                assertThat(댓글.isDeleted()).isFalse();
            });
        }

        @Test
        void 댓글_제거_시_자식_댓글이_존재한다면_부모와의_연관관계는_유지되며_논리적으로만_제거시킨다() {
            // given
            Long 댓글_ID = 비인증_댓글을_작성한다(postId.getPostId(), blogName, "말랑 댓글", "hi", "1");
            Long 대댓글_ID = 대댓글을_작성한다(postId.getPostId(), blogName, "대댓글", false, postWriterId,
                    댓글_ID);
            DeleteUnAuthCommentCommand command = DeleteUnAuthCommentCommand.builder()
                    .commentId(댓글_ID)
                    .password("1")
                    .build();

            // when
            unAuthCommentService.delete(command);

            // then
            transactionHelper.doAssert(() -> {
                Comment 제거된_말랑_댓글 = 비인증_댓글을_조회한다(댓글_ID);
                Comment 대댓글 = 인증된_댓글을_조회한다(대댓글_ID);
                assertThat(대댓글.getParent()).isEqualTo(제거된_말랑_댓글);
                assertThat(대댓글.isDeleted()).isFalse();
                assertThat(제거된_말랑_댓글.isDeleted()).isTrue();
                assertThat(제거된_말랑_댓글.getChildren().get(0)).isEqualTo(대댓글);
            });
        }

        @Test
        void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태이며_더이상_존재하는_자식이_없는_경우_부모_댓글도_물리적으로_제거된다() {
            // given
            Long 댓글_ID = 비인증_댓글을_작성한다(postId.getPostId(), blogName, "말랑 댓글", "hi", "12");
            Long 대댓글_ID = 비인증_대댓글을_작성한다(postId.getPostId(), blogName, "대댓글", "hi2", "12", 댓글_ID);
            unAuthCommentService.delete(new DeleteUnAuthCommentCommand(댓글_ID, "12", null, null));
            DeleteUnAuthCommentCommand command = DeleteUnAuthCommentCommand.builder()
                    .commentId(대댓글_ID)
                    .password("12")
                    .build();

            // when
            unAuthCommentService.delete(command);

            // then
            assertThatThrownBy(() ->
                    비인증_댓글을_조회한다(댓글_ID)
            ).isInstanceOf(NotFoundCommentException.class);
            assertThatThrownBy(() ->
                    비인증_댓글을_조회한다(대댓글_ID)
            ).isInstanceOf(NotFoundCommentException.class);
        }
    }
}
