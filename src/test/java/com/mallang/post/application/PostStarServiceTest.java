package com.mallang.post.application;

import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CancelPostStarCommand;
import com.mallang.post.application.command.CreateStarGroupCommand;
import com.mallang.post.application.command.StarPostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostVisibilityPolicy;
import com.mallang.post.domain.star.PostStar;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.exception.NoAuthorityStarGroupException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DisplayName("포스트 즐겨찾기 서비스 (PostStarService) 은(는)")
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
                new PostVisibilityPolicy(PUBLIC, null)
        ).getPostId();
        protectedPostId = 포스트를_저장한다(
                memberId,
                blogName,
                "포스트",
                "내용",
                new PostVisibilityPolicy(PROTECTED, "1234")
        ).getPostId();
        privatePostId = 포스트를_저장한다(
                memberId,
                blogName,
                "포스트",
                "내용",
                new PostVisibilityPolicy(PRIVATE, null)
        ).getPostId();
    }

    @Nested
    class 즐겨찾기_시 {

        @Test
        void 로그인해야_즐겨찾기를_누를_수_있다() {
            // given
            StarPostCommand command = new StarPostCommand(publicPostId, blogName, null, null, null);

            // when & then
            assertThatThrownBy(() -> {
                postStarService.star(command);
            }).isInstanceOf(InvalidDataAccessApiUsageException.class);
        }

        @Test
        void 즐겨찾기_시_즐켜찾기_그룹을_설정할_수_있다() {
            // given
            var createStarGroupCommand = new CreateStarGroupCommand(memberId, "Spring", null, null, null);
            var springStarGroupId = starGroupService.create(createStarGroupCommand);
            var command = new StarPostCommand(publicPostId, blogName, springStarGroupId, memberId, null);

            // when
            Long starId = postStarService.star(command);

            // then
            PostStar find = postStarRepository.getById(starId);
            assertThat(find.getStarGroup().getId())
                    .isEqualTo(springStarGroupId);
        }

        @Test
        void 회원이_이미_해당_포스트에_즐겨찾기를_누른_경우_그룹이_업데이트된다() {
            // given
            var createStarGroupCommand = new CreateStarGroupCommand(memberId, "Spring", null, null, null);
            var springStarGroupId = starGroupService.create(createStarGroupCommand);
            var starPostCommand = new StarPostCommand(publicPostId, blogName, springStarGroupId, memberId, null);
            Long starId = postStarService.star(starPostCommand);

            StarPostCommand command = new StarPostCommand(publicPostId, blogName, null, memberId, null);

            // when
            postStarService.star(command);

            // then
            PostStar postStar = postStarRepository.getById(starId);
            assertThat(postStar.getStarGroup()).isNull();
        }

        @Test
        void 타인의_즐겨찾기_그룹을_설정한_경우_예외() {
            // given
            var createStarGroupCommand = new CreateStarGroupCommand(otherMemberId, "Spring", null, null, null);
            var springStarGroupId = starGroupService.create(createStarGroupCommand);
            var command = new StarPostCommand(publicPostId, blogName, springStarGroupId, memberId, null);

            // when & then
            assertThatThrownBy(() ->
                    postStarService.star(command)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 해당_포스트에_즐겨찾기를_누른_적이_없으면_즐겨찾기를_누른다() {
            // when
            Long starId = postStarService.star(new StarPostCommand(publicPostId, blogName, null, memberId, null));

            // then
            assertThat(starId).isNotNull();
        }

        @Test
        void 글_작성자는_보호된_글에_즐겨찾기를_누를_수_있다() {
            // when
            Long starId = postStarService.star(new StarPostCommand(protectedPostId, blogName, null, memberId, null));

            // then
            assertThat(starId).isNotNull();
        }

        @Test
        void 보호된_글의_비밀번호와_입력한_비밀번호가_일치하면_즐겨찾기를_누를_수_있다() {
            // when
            Long starId = postStarService.star(
                    new StarPostCommand(protectedPostId, blogName, null, otherMemberId, "1234")
            );

            // then
            assertThat(starId).isNotNull();
        }

        @Test
        void 보호된_글의_비밀번호와_입력한_비밀번호가_다르면_예외() {
            // given
            StarPostCommand command = new StarPostCommand(protectedPostId, blogName, null, otherMemberId, "12345");

            // when & then
            assertThatThrownBy(() -> {
                postStarService.star(command);
            }).isInstanceOf(NoAuthorityPostException.class);
        }

        @Test
        void 비공개_글에는_작성자_말고는_즐겨찾기를_누를_수_없다() {
            // given
            StarPostCommand command1 = new StarPostCommand(privatePostId, blogName, null, memberId, null);
            StarPostCommand command2 = new StarPostCommand(privatePostId, blogName, null, otherMemberId, null);

            // when & then
            assertDoesNotThrow(() -> {
                postStarService.star(command1);
            });
            assertThatThrownBy(() -> {
                postStarService.star(command2);
            }).isInstanceOf(NoAuthorityPostException.class);
        }
    }

    @Nested
    class 즐겨찾기_취소_시 {

        @Test
        void 즐겨찾기_취소_시_즐겨찾기가_제거된다() {
            // given
            postStarService.star(new StarPostCommand(publicPostId, blogName, null, memberId, null));

            // when
            postStarService.cancel(new CancelPostStarCommand(memberId, publicPostId, blogName));

            // then
            Post post = postRepository.getById(publicPostId, blogName);
            assertThat(post.getLikeCount()).isZero();
        }

        @Test
        void 보호글_비공개글_여부에_관계없이_취소할_수_있다() {
            // given
            postStarService.star(new StarPostCommand(protectedPostId, blogName, null, otherMemberId, "1234"));
            postStarService.star(new StarPostCommand(publicPostId, blogName, null, otherMemberId, null));
            포스트_공개여부를_업데이트한다(memberId, publicPostId, blogName, PRIVATE, null);

            // when & then
            assertDoesNotThrow(() -> {
                postStarService.cancel(new CancelPostStarCommand(otherMemberId, protectedPostId, blogName));
            });
            assertDoesNotThrow(() -> {
                postStarService.cancel(new CancelPostStarCommand(otherMemberId, publicPostId, blogName));
            });
        }
    }
}
