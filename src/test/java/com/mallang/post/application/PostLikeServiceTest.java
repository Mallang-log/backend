package com.mallang.post.application;

import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CancelPostLikeCommand;
import com.mallang.post.application.command.ClickPostLikeCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostVisibilityPolicy;
import com.mallang.post.exception.AlreadyLikedPostException;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DisplayName("포스트 좋아요 서비스 (PostLikeService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostLikeServiceTest extends ServiceTest {

    private Long memberId;
    private Long otherMemberId;
    private String blogName;
    private Long publicPostId;
    private Long protectedPostId;
    private Long privatePostId;

    @BeforeEach
    void setUp() {
        memberId = 회원을_저장한다("말랑");
        otherMemberId = 회원을_저장한다("other");
        blogName = 블로그_개설(memberId, "mallang-log");
        publicPostId = 포스트를_저장한다(
                memberId,
                blogName,
                "포스트",
                "내용",
                new PostVisibilityPolicy(PUBLIC, null)
        ).getId();
        protectedPostId = 포스트를_저장한다(
                memberId,
                blogName,
                "포스트",
                "내용",
                new PostVisibilityPolicy(PROTECTED, "1234")
        ).getId();
        privatePostId = 포스트를_저장한다(
                memberId,
                blogName,
                "포스트",
                "내용",
                new PostVisibilityPolicy(PRIVATE, null)
        ).getId();
    }

    @Nested
    class 좋아요_시 {

        @Test
        void 로그인해야_좋아요를_누를_수_있다() {
            // given
            ClickPostLikeCommand command = new ClickPostLikeCommand(publicPostId, blogName, null, null);

            // when & then
            assertThatThrownBy(() -> {
                postLikeService.like(command);
            }).isInstanceOf(InvalidDataAccessApiUsageException.class);
        }

        @Test
        void 회원이_이미_해당_포스트에_좋아요를_누른_경우_예외() {
            // given
            postLikeService.like(new ClickPostLikeCommand(publicPostId, blogName, memberId, null));
            ClickPostLikeCommand command = new ClickPostLikeCommand(publicPostId, blogName, memberId, null);

            // when
            assertThatThrownBy(() -> {
                postLikeService.like(command);
            }).isInstanceOf(AlreadyLikedPostException.class);

            // then
            Post post = postRepository.getById(publicPostId, blogName);
            assertThat(post.getLikeCount()).isEqualTo(1);
        }

        @Test
        void 해당_포스트에_좋아요를_누른_적이_없으면_좋아요를_누른다() {
            // when
            postLikeService.like(new ClickPostLikeCommand(publicPostId, blogName, memberId, null));

            // then
            Post post = postRepository.getById(publicPostId, blogName);
            assertThat(post.getLikeCount()).isEqualTo(1);
        }

        @Test
        void 글_작성자는_보호된_글에_좋아요를_누를_수_있다() {
            // when
            postLikeService.like(new ClickPostLikeCommand(protectedPostId, blogName, memberId, null));

            // then
            Post post = postRepository.getById(protectedPostId, blogName);
            assertThat(post.getLikeCount()).isEqualTo(1);
        }

        @Test
        void 보호된_글의_비밀번호와_입력한_비밀번호가_일치하면_좋아요를_누를_수_있다() {
            // when
            postLikeService.like(new ClickPostLikeCommand(protectedPostId, blogName, otherMemberId, "1234"));

            // then
            Post post = postRepository.getById(protectedPostId, blogName);
            assertThat(post.getLikeCount()).isEqualTo(1);
        }

        @Test
        void 보호된_글의_비밀번호와_입력한_비밀번호가_다르면_예외() {
            // given
            ClickPostLikeCommand command = new ClickPostLikeCommand(protectedPostId, blogName, otherMemberId, "12345");

            // when & then
            assertThatThrownBy(() -> {
                postLikeService.like(command);
            }).isInstanceOf(NoAuthorityAccessPostException.class);
        }

        @Test
        void 비공개_글에는_작성자_말고는_좋아요를_누를_수_없다() {
            // given
            ClickPostLikeCommand command1 = new ClickPostLikeCommand(privatePostId, blogName, memberId, null);
            ClickPostLikeCommand command2 = new ClickPostLikeCommand(privatePostId, blogName, otherMemberId, null);

            // when & then
            assertDoesNotThrow(() -> {
                postLikeService.like(command1);
            });
            assertThatThrownBy(() -> {
                postLikeService.like(command2);
            }).isInstanceOf(NoAuthorityAccessPostException.class);
        }
    }

    @Nested
    class 좋아요_취소_시 {

        @Test
        void 좋아요_취소_시_좋아요가_제거된다() {
            // given
            postLikeService.like(new ClickPostLikeCommand(publicPostId, blogName, memberId, null));

            // when
            postLikeService.cancel(new CancelPostLikeCommand(publicPostId, blogName, memberId, null));

            // then
            Post post = postRepository.getById(publicPostId, blogName);
            assertThat(post.getLikeCount()).isZero();
        }

        @Test
        void 글_작성자는_보호된_글에_누른_좋아요를_취소할_수_있다() {
            // given
            postLikeService.like(new ClickPostLikeCommand(publicPostId, blogName, memberId, null));
            포스트_공개여부를_업데이트한다(memberId, publicPostId, blogName, PROTECTED, "1234");

            // when
            postLikeService.cancel(new CancelPostLikeCommand(publicPostId, blogName, memberId, null));

            // then
            Post post = postRepository.getById(publicPostId, blogName);
            assertThat(post.getLikeCount()).isZero();
        }

        @Test
        void 보호된_글의_비밀번호와_입력한_비밀번호가_일치하면_좋아요를_취소한_수_있다() {
            // given
            postLikeService.like(new ClickPostLikeCommand(publicPostId, blogName, otherMemberId, null));
            포스트_공개여부를_업데이트한다(memberId, publicPostId, blogName, PROTECTED, "1234");

            // when
            postLikeService.cancel(new CancelPostLikeCommand(publicPostId, blogName, otherMemberId, "1234"));

            // then
            Post post = postRepository.getById(publicPostId, blogName);
            assertThat(post.getLikeCount()).isZero();
        }

        @Test
        void 보호된_글의_비밀번호와_입력한_비밀번호가_다르면_예외() {
            // given
            postLikeService.like(new ClickPostLikeCommand(publicPostId, blogName, otherMemberId, null));
            포스트_공개여부를_업데이트한다(memberId, publicPostId, blogName, PROTECTED, "1234");

            // when & then
            assertThatThrownBy(() -> {
                postLikeService.cancel(new CancelPostLikeCommand(publicPostId, blogName, otherMemberId, "12345"));
            }).isInstanceOf(NoAuthorityAccessPostException.class);
        }

        @Test
        void 비공개_글에는_작성자_말고는_좋아요를_취소할_수_없다() {
            // given
            postLikeService.like(new ClickPostLikeCommand(publicPostId, blogName, memberId, null));
            postLikeService.like(new ClickPostLikeCommand(publicPostId, blogName, otherMemberId, null));
            포스트_공개여부를_업데이트한다(memberId, publicPostId, blogName, PRIVATE, null);

            // when & then
            assertDoesNotThrow(() -> {
                postLikeService.cancel(new CancelPostLikeCommand(publicPostId, blogName, memberId, null));
            });
            assertThatThrownBy(() -> {
                postLikeService.cancel(new CancelPostLikeCommand(publicPostId, blogName, otherMemberId, null));
            }).isInstanceOf(NoAuthorityAccessPostException.class);
        }
    }
}
