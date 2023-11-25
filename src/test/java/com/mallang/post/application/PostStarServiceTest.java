package com.mallang.post.application;

import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CancelPostStarCommand;
import com.mallang.post.application.command.StarPostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.exception.AlreadyStarPostException;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DisplayName("포스트 즐겨찾기 서비스(PostStarService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostStarServiceTest extends ServiceTest {

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
                new PostVisibilityPolicy(PUBLIC, null));
        protectedPostId = 포스트를_저장한다(
                memberId,
                blogName,
                "포스트",
                "내용",
                new PostVisibilityPolicy(PROTECTED, "1234"));
        privatePostId = 포스트를_저장한다(
                memberId,
                blogName,
                "포스트",
                "내용",
                new PostVisibilityPolicy(PRIVATE, null));
    }

    @Nested
    class 즐겨찾기_시 {

        @Test
        void 로그인해야_즐겨찾기를_누를_수_있다() {
            // given
            StarPostCommand command = new StarPostCommand(publicPostId, null, null);

            // when & then
            assertThatThrownBy(() -> {
                postStarService.star(command);
            }).isInstanceOf(InvalidDataAccessApiUsageException.class);
        }

        @Test
        void 회원이_이미_해당_포스트에_즐겨찾기를_누른_경우_예외() {
            // given
            postStarService.star(new StarPostCommand(publicPostId, memberId, null));
            StarPostCommand command = new StarPostCommand(publicPostId, memberId, null);

            // when & then
            assertThatThrownBy(() -> {
                postStarService.star(command);
            }).isInstanceOf(AlreadyStarPostException.class);
        }

        @Test
        void 해당_포스트에_즐겨찾기를_누른_적이_없으면_즐겨찾기를_누른다() {
            // when
            Long starId = postStarService.star(new StarPostCommand(publicPostId, memberId, null));

            // then
            assertThat(starId).isNotNull();
        }

        @Test
        void 글_작성자는_보호된_글에_즐겨찾기를_누를_수_있다() {
            // when
            Long starId = postStarService.star(new StarPostCommand(protectedPostId, memberId, null));

            // then
            assertThat(starId).isNotNull();
        }

        @Test
        void 보호된_글의_비밀번호와_입력한_비밀번호가_일치하면_즐겨찾기를_누를_수_있다() {
            // when
            Long starId = postStarService.star(new StarPostCommand(protectedPostId, otherMemberId, "1234"));

            // then
            assertThat(starId).isNotNull();
        }

        @Test
        void 보호된_글의_비밀번호와_입력한_비밀번호가_다르면_예외() {
            // given
            StarPostCommand command = new StarPostCommand(protectedPostId, otherMemberId, "12345");

            // when & then
            assertThatThrownBy(() -> {
                postStarService.star(command);
            }).isInstanceOf(NoAuthorityAccessPostException.class);
        }

        @Test
        void 비공개_글에는_작성자_말고는_즐겨찾기를_누를_수_없다() {
            // given
            StarPostCommand command1 = new StarPostCommand(privatePostId, memberId, null);
            StarPostCommand command2 = new StarPostCommand(privatePostId, otherMemberId, null);

            // when & then
            assertDoesNotThrow(() -> {
                postStarService.star(command1);
            });
            assertThatThrownBy(() -> {
                postStarService.star(command2);
            }).isInstanceOf(NoAuthorityAccessPostException.class);
        }
    }

    @Nested
    class 즐겨찾기_취소_시 {

        @Test
        void 즐겨찾기_취소_시_즐겨찾기가_제거된다() {
            // given
            postStarService.star(new StarPostCommand(publicPostId, memberId, null));

            // when
            postStarService.cancel(new CancelPostStarCommand(publicPostId, memberId));

            // then
            Post post = postRepository.getById(publicPostId);
            assertThat(post.getLikeCount()).isZero();
        }

        @Test
        void 보호글_비공개글_여부에_관계없이_취소할_수_있다() {
            // given
            postStarService.star(new StarPostCommand(protectedPostId, otherMemberId, "1234"));
            postStarService.star(new StarPostCommand(publicPostId, otherMemberId, null));
            포스트_공개여부를_업데이트한다(memberId, publicPostId, PRIVATE, null);

            // when & then
            assertDoesNotThrow(() -> {
                postStarService.cancel(new CancelPostStarCommand(protectedPostId, otherMemberId));
            });
            assertDoesNotThrow(() -> {
                postStarService.cancel(new CancelPostStarCommand(publicPostId, otherMemberId));
            });
        }
    }
}
