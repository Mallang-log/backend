package com.mallang.post.application;

import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.category.exception.NoAuthorityCategoryException;
import com.mallang.category.exception.NotFoundCategoryException;
import com.mallang.common.EventsTestUtils;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CreateDraftCommand;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.DeletePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostDeleteEvent;
import com.mallang.post.domain.PostId;
import com.mallang.post.exception.NoAuthorityPostException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


@DisplayName("포스트 서비스 (PostService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostServiceTest extends ServiceTest {

    private Long memberId;
    private String blogName;

    @Nested
    class 포스트_저장_시 {

        @BeforeEach
        void setUp() {
            memberId = 회원을_저장한다("말랑");
            blogName = 블로그_개설(memberId, "mallang-log");
        }

        @Test
        void 다른_사람의_블로그에_대한_포스트를_작성시_예외() {
            // given
            Long otherMemberId = 회원을_저장한다("other");
            String otherBlogName = 블로그_개설(otherMemberId, "other-log");
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(otherBlogName)
                    .title("포스트 1")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .bodyText("bodyText")
                    .build();
            CreatePostCommand command2 = CreatePostCommand.builder()
                    .memberId(otherMemberId)
                    .blogName(blogName)
                    .title("포스트 1")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .bodyText("bodyText")
                    .build();

            // when & then
            assertThatThrownBy(() -> {
                postService.create(command);
            }).isInstanceOf(NoAuthorityBlogException.class);
            assertThatThrownBy(() -> {
                postService.create(command2);
            }).isInstanceOf(NoAuthorityBlogException.class);
        }

        @Test
        void 카테고리_없는_포스트를_저장한다() {
            // given
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("포스트 1")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .bodyText("bodyText")
                    .build();

            // when
            Long id = postService.create(command).getId();

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 카테고리를_설정할_수_있다() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    memberId, blogName, "Spring", null
            ));
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("포스트 1")
                    .bodyText("bodyText")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .categoryId(categoryId)
                    .build();

            // when
            Long id = postService.create(command).getId();

            // then
            transactionHelper.doAssert(() -> {
                Post post = postRepository.getById(id, blogName);
                assertThat(post.getCategory().getName()).isEqualTo("Spring");
            });
        }

        @Test
        void 없는_카테고리면_예외() {
            // given
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("포스트 1")
                    .bodyText("bodyText")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .categoryId(1000L)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postService.create(command)
            ).isInstanceOf(NotFoundCategoryException.class);
        }

        @Test
        void 다른_사람의_카테고리라면_예외() {
            // given
            Long otherMemberId = 회원을_저장한다("other");
            String otherBlogName = 블로그_개설(otherMemberId, "other-log");
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    otherMemberId, otherBlogName, "Spring", null
            ));
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("포스트 1")
                    .bodyText("bodyText")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .categoryId(categoryId)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postService.create(command)
            ).isInstanceOf(NoAuthorityCategoryException.class);
        }

        @Test
        void 태그를_함께_저장한다() {
            // given
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("포스트 1")
                    .bodyText("bodyText")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .tags(List.of("tag1", "tag2", "tag3"))
                    .build();

            // when
            Long id = postService.create(command).getId();

            // then
            transactionHelper.doAssert(() -> {
                Post post = postRepository.getById(id, blogName);
                assertThat(post.getTags())
                        .containsExactly("tag1", "tag2", "tag3");
            });
        }
    }

    @Nested
    class 임시_글로부터_포스트_생성_시 {

        private Long draftId;

        @BeforeEach
        void setUp() {
            memberId = 회원을_저장한다("말랑");
            blogName = 블로그_개설(memberId, "mallang-log");
            CreateDraftCommand createDraftCommand = CreateDraftCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("임시_글 1")
                    .bodyText("bodyText")
                    .intro("intro")
                    .tags(List.of("tag1"))
                    .build();
            draftId = draftService.create(createDraftCommand);
        }

        @Test
        void 포스트는_생성되고_임시_글은_제거된다() {
            // given
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("임시 글로부터 작성된 포스트")
                    .bodyText("bodyText")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .tags(List.of("tag1"))
                    .build();

            // when
            PostId postId = postService.createFromDraft(command, draftId);

            // then
            assertThat(postId).isNotNull();
            assertThat(draftRepository.existsById(draftId)).isFalse();
        }

        @Test
        void 다른_사람의_블로그에_작성하는_경우_예외() {
            // given
            Long otherMemberId = 회원을_저장한다("other");
            String otherBlogName = 블로그_개설(otherMemberId, "other-log");
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(otherBlogName)
                    .title("임시 글로부터 작성된 포스트")
                    .bodyText("bodyText")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .tags(List.of("tag1"))
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postService.createFromDraft(command, draftId)
            ).isInstanceOf(NoAuthorityBlogException.class);
        }

        @Test
        void 다른_블로그의_임시_글을_통해_생성하려는_경우_예외() {
            // given
            Long otherMemberId = 회원을_저장한다("other");
            String otherBlogName = 블로그_개설(otherMemberId, "other-log");
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(otherMemberId)
                    .blogName(otherBlogName)
                    .title("임시 글로부터 작성된 포스트")
                    .bodyText("bodyText")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .tags(List.of("tag1"))
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postService.createFromDraft(command, draftId)
            ).isInstanceOf(NoAuthorityPostException.class);
        }
    }

    @Nested
    class 포스트_수정_시 {

        @BeforeEach
        void setUp() {
            memberId = 회원을_저장한다("말랑");
            blogName = 블로그_개설(memberId, "mallang-log");
        }

        @Test
        void 내가_쓴_포스트를_수정할_수_있다() {
            // given
            Long 포스트_ID = 포스트를_저장한다(memberId, blogName, "포스트", "내용", "태그1").getId();

            // when
            postService.update(new UpdatePostCommand(
                    memberId, 포스트_ID, blogName,
                    "수정제목", "수정내용",
                    "수정썸네일",
                    "수정인트로",
                    PUBLIC, null,
                    null, List.of("태그2")));

            // then
            transactionHelper.doAssert(() -> {
                Post post = postRepository.getById(포스트_ID, blogName);
                assertThat(post.getTitle()).isEqualTo("수정제목");
                assertThat(post.getBodyText()).isEqualTo("수정내용");
                assertThat(post.getPostThumbnailImageName()).isEqualTo("수정썸네일");
                assertThat(post.getPostIntro()).isEqualTo("수정인트로");
                assertThat(post.getTags())
                        .containsExactly("태그2");
            });
        }

        @Test
        void 다른_사람의_포스트는_수정할_수_없다() {
            // given
            Long otherMemberId = 회원을_저장한다("동훈");
            Long 포스트_ID = 포스트를_저장한다(memberId, blogName, "포스트", "내용").getId();

            // when
            assertThatThrownBy(() ->
                    postService.update(
                            new UpdatePostCommand(
                                    otherMemberId, 포스트_ID, blogName,
                                    "수정제목", "수정내용",
                                    null, "수정인트로",
                                    PUBLIC, null,
                                    null, emptyList()))
            ).isInstanceOf(NoAuthorityPostException.class);

            // then
            Post post = postRepository.getById(포스트_ID, blogName);
            assertThat(post.getTitle()).isEqualTo("포스트");
            assertThat(post.getBodyText()).isEqualTo("내용");
        }

        @Test
        void 포스트_수정_시_있던_카테고리릴_없앨_수_있다() {
            // given
            Long springCategoryId = categoryService.create(new CreateCategoryCommand(
                    memberId, blogName, "Spring", null
            ));
            Long 포스트_ID = 포스트를_저장한다(memberId, blogName, "포스트", "내용", springCategoryId).getId();

            // when
            postService.update(new UpdatePostCommand(
                    memberId, 포스트_ID, blogName,
                    "수정제목", "수정내용",
                    null, "수정인트로",
                    PUBLIC, null,
                    null, emptyList()));

            // then
            Post post = postRepository.getById(포스트_ID, blogName);
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getBodyText()).isEqualTo("수정내용");
            assertThat(post.getCategory()).isNull();
        }

        @Test
        void 포스트_수정_시_없던_카테고리를_설정할_수_있다() {
            // given
            Long 포스트_ID = 포스트를_저장한다(memberId, blogName, "포스트", "내용").getId();
            Long springCategoryId = categoryService.create(new CreateCategoryCommand(
                    memberId, blogName, "Spring", null
            ));

            // when
            postService.update(
                    new UpdatePostCommand(
                            memberId, 포스트_ID, blogName,
                            "수정제목", "수정내용",
                            null, "수정인트로",
                            PUBLIC, null,
                            springCategoryId, emptyList()));

            // then
            transactionHelper.doAssert(() -> {
                Post post = postRepository.getById(포스트_ID, blogName);
                assertThat(post.getTitle()).isEqualTo("수정제목");
                assertThat(post.getBodyText()).isEqualTo("수정내용");
                assertThat(post.getCategory().getName()).isEqualTo("Spring");
            });
        }

        @Test
        void 기존_카테고리를_다른_카테고리로_변경할_수_있다() {
            // given
            Long springCategoryId = categoryService.create(new CreateCategoryCommand(
                    memberId, blogName, "Spring", null
            ));
            Long 포스트_ID = 포스트를_저장한다(memberId, blogName, "포스트", "내용", springCategoryId).getId();
            Long nodeCategoryId = categoryService.create(new CreateCategoryCommand(memberId, blogName, "Node", null));

            // when
            postService.update(new UpdatePostCommand(
                    memberId, 포스트_ID, blogName,
                    "수정제목", "수정내용",
                    null, "수정인트로",
                    PUBLIC, null,
                    nodeCategoryId, emptyList()));

            // then
            transactionHelper.doAssert(() -> {
                Post post = postRepository.getById(포스트_ID, blogName);
                assertThat(post.getTitle()).isEqualTo("수정제목");
                assertThat(post.getBodyText()).isEqualTo("수정내용");
                assertThat(post.getCategory().getName()).isEqualTo("Node");
            });
        }
    }

    @Nested
    class 포스트_제거_시 {

        private PostId myPostId1;
        private PostId myPostId2;
        private Long otherId;
        private String otherBlogName;
        private PostId otherPostId;

        @BeforeEach
        void setUp() {
            memberId = 회원을_저장한다("말랑");
            blogName = 블로그_개설(memberId, "mallang-log");
            myPostId1 = 포스트를_저장한다(memberId, blogName, "내 글 1", "내 글 1 입니다.");
            myPostId2 = 포스트를_저장한다(memberId, blogName, "내 글 2", "내 글 2 입니다.");
            댓글을_작성한다(myPostId1.getId(), blogName, "dw", false, memberId);
            otherId = 회원을_저장한다("other");
            otherBlogName = 블로그_개설(otherId, "other-log");
            otherPostId = 포스트를_저장한다(otherId, otherBlogName, "다른사람 글 1", "다른사람 글 1 입니다.");
        }

        @Test
        void 자신이_작성한_글이_아닌_경우_예외() {
            // given
            DeletePostCommand command = new DeletePostCommand(otherId, List.of(myPostId1.getId()), blogName);

            // when
            assertThatThrownBy(() -> {
                postService.delete(command);
            }).isInstanceOf(NoAuthorityPostException.class);

            // then
            assertThat(postRepository.findById(myPostId1.getId(), blogName)).isPresent();
            assertThat(EventsTestUtils.count(events, PostDeleteEvent.class)).isZero();
        }

        @Test
        void 없는_글이_있으면_제외하고_제거된다() {
            // when
            postService.delete(new DeletePostCommand(memberId, List.of(myPostId1.getId(), 100000L), blogName));

            // then
            assertThat(postRepository.findById(myPostId1.getId(), blogName)).isEmpty();
            assertThat(EventsTestUtils.count(events, PostDeleteEvent.class)).isEqualTo(1);
        }

        @Test
        void 원하는_포스트들을_제거하며_각각_댓글_제거_이벤트가_발행된다() {
            // when
            postService.delete(
                    new DeletePostCommand(memberId, List.of(myPostId1.getId(), myPostId2.getId()), blogName));

            // then
            assertThat(postRepository.findById(myPostId1.getId(), blogName)).isEmpty();
            assertThat(postRepository.findById(myPostId2.getId(), blogName)).isEmpty();
            assertThat(postRepository.findById(otherPostId.getId(), otherBlogName)).isPresent();
            assertThat(EventsTestUtils.count(events, PostDeleteEvent.class)).isEqualTo(2);
        }
    }
}
