package com.mallang.post.application;

import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.auth.MemberServiceTestHelper;
import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.category.exception.NotFoundCategoryException;
import com.mallang.comment.application.CommentServiceTestHelper;
import com.mallang.common.EventsTestUtils;
import com.mallang.common.ServiceTest;
import com.mallang.common.TransactionHelper;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.DeletePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostDeleteEvent;
import com.mallang.post.domain.Tag;
import com.mallang.post.exception.NotFoundPostException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

@DisplayName("포스트 서비스(PostService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class PostServiceTest {

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private BlogServiceTestHelper blogServiceTestHelper;

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Autowired
    private CategoryServiceTestHelper categoryServiceTestHelper;

    @Autowired
    private PostService postService;

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private ApplicationEvents events;

    private Long memberId;
    private Long blogId;

    @Nested
    class 포스트_저장_시 {

        @BeforeEach
        void setUp() {
            memberId = memberServiceTestHelper.회원을_저장한다("말랑");
            blogId = blogServiceTestHelper.블로그_개설후_ID_반환(memberId, "mallang-log");
        }

        @Test
        void 카테고리_없는_포스트를_저장한다() {
            // given
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogId(blogId)
                    .title("포스트 1")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .content("content")
                    .build();

            // when
            Long id = postService.create(command);

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 카테고리를_설정할_수_있다() {
            // given
            Long categoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogId, "Spring");
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogId(blogId)
                    .title("포스트 1")
                    .content("content")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .categoryId(categoryId)
                    .build();

            // when
            Long id = postService.create(command);

            // then
            transactionHelper.doAssert(() -> {
                Post post = postServiceTestHelper.포스트를_조회한다(id);
                assertThat(post.getCategory().getName()).isEqualTo("Spring");
            });
        }

        @Test
        void 없는_카테고리면_예외() {
            // given
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogId(blogId)
                    .title("포스트 1")
                    .content("content")
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
        void 태그를_함께_저장한다() {
            // given
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogId(blogId)
                    .title("포스트 1")
                    .content("content")
                    .intro("intro")
                    .visibility(PUBLIC)
                    .tags(List.of("tag1", "tag2", "tag3"))
                    .build();

            // when
            Long id = postService.create(command);

            // then
            transactionHelper.doAssert(() -> {
                Post post = postServiceTestHelper.포스트를_조회한다(id);
                assertThat(post.getTags())
                        .extracting(Tag::getContent)
                        .containsExactly("tag1", "tag2", "tag3");
            });
        }
    }

    @Nested
    class 포스트_수정_시 {

        @BeforeEach
        void setUp() {
            memberId = memberServiceTestHelper.회원을_저장한다("말랑");
            blogId = blogServiceTestHelper.블로그_개설후_ID_반환(memberId, "mallang-log");
        }

        @Test
        void 내가_쓴_포스트를_수정할_수_있다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트", "내용", "태그1");

            // when
            postService.update(new UpdatePostCommand(memberId, 포스트_ID,
                    "수정제목", "수정내용",
                    "수정썸네일",
                    "수정인트로",
                    PUBLIC, null,
                    null, List.of("태그2")));

            // then
            transactionHelper.doAssert(() -> {
                Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
                assertThat(post.getTitle()).isEqualTo("수정제목");
                assertThat(post.getContent()).isEqualTo("수정내용");
                assertThat(post.getPostThumbnailImageName()).isEqualTo("수정썸네일");
                assertThat(post.getPostIntro()).isEqualTo("수정인트로");
                assertThat(post.getTags()).extracting(Tag::getContent)
                        .containsExactly("태그2");
            });
        }

        @Test
        void 다른_사람의_포스트는_수정할_수_없다() {
            // given
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("동훈");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트", "내용");

            // when
            assertThatThrownBy(() ->
                    postService.update(
                            new UpdatePostCommand(otherMemberId, 포스트_ID,
                                    "수정제목", "수정내용",
                                    null, "수정인트로",
                                    PUBLIC, null,
                                    null, emptyList()))
            ).isInstanceOf(NotFoundPostException.class);

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
            assertThat(post.getTitle()).isEqualTo("포스트");
            assertThat(post.getContent()).isEqualTo("내용");
        }

        @Test
        void 포스트_수정_시_있던_카테고리릴_없앨_수_있다() {
            // given
            Long springCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogId, "Spring");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트", "내용", springCategoryId);

            // when
            postService.update(new UpdatePostCommand(memberId, 포스트_ID,
                    "수정제목", "수정내용",
                    null, "수정인트로",
                    PUBLIC, null,
                    null, emptyList()));

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getContent()).isEqualTo("수정내용");
            assertThat(post.getCategory()).isNull();
        }

        @Test
        void 포스트_수정_시_없던_카테고리를_설정할_수_있다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트", "내용");
            Long springCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogId, "Spring");

            // when
            postService.update(
                    new UpdatePostCommand(memberId, 포스트_ID,
                            "수정제목", "수정내용",
                            null, "수정인트로",
                            PUBLIC, null,
                            springCategoryId, emptyList()));

            // then
            transactionHelper.doAssert(() -> {
                Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
                assertThat(post.getTitle()).isEqualTo("수정제목");
                assertThat(post.getContent()).isEqualTo("수정내용");
                assertThat(post.getCategory().getName()).isEqualTo("Spring");
            });
        }

        @Test
        void 기존_카테고리를_다른_카테고리로_변경할_수_있다() {
            // given
            Long springCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogId, "Spring");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트", "내용", springCategoryId);
            Long nodeCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogId, "Node");

            // when
            postService.update(new UpdatePostCommand(
                    memberId,
                    포스트_ID,
                    "수정제목", "수정내용",
                    null, "수정인트로",
                    PUBLIC, null,
                    nodeCategoryId, emptyList()));

            // then
            transactionHelper.doAssert(() -> {
                Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
                assertThat(post.getTitle()).isEqualTo("수정제목");
                assertThat(post.getContent()).isEqualTo("수정내용");
                assertThat(post.getCategory().getName()).isEqualTo("Node");
            });
        }
    }

    @Autowired
    private CommentServiceTestHelper commentServiceTestHelper;

    @Nested
    class 포스트_제거_시 {

        private Long myPostId1;
        private Long myPostId2;
        private Long otherId;
        private Long otherPostId;

        @BeforeEach
        void setUp() {
            memberId = memberServiceTestHelper.회원을_저장한다("말랑");
            blogId = blogServiceTestHelper.블로그_개설후_ID_반환(memberId, "mallang-log");
            myPostId1 = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "내 글 1", "내 글 1 입니다.");
            myPostId2 = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "내 글 2", "내 글 2 입니다.");
            commentServiceTestHelper.댓글을_작성한다(myPostId1, "dw", false, memberId);
            otherId = memberServiceTestHelper.회원을_저장한다("other");
            Long otherBlogId = blogServiceTestHelper.블로그_개설후_ID_반환(otherId, "other-log");
            otherPostId = postServiceTestHelper.포스트를_저장한다(otherId, otherBlogId, "다른사람 글 1", "다른사람 글 1 입니다.");
        }

        @Test
        void 자신이_작성한_글이_아닌_경우_아무것도_지워지지_않는다() {
            // when
            postService.delete(new DeletePostCommand(otherId, List.of(myPostId1)));

            // then
            assertThat(postServiceTestHelper.포스트_존재여부_확인(myPostId1)).isTrue();
            assertThat(EventsTestUtils.count(events, PostDeleteEvent.class)).isZero();
        }

        @Test
        void 없는_글이_있으면_제외하고_제거된다() {
            // when
            postService.delete(new DeletePostCommand(memberId, List.of(myPostId1, 100000L)));

            // then
            assertThat(postServiceTestHelper.포스트_존재여부_확인(myPostId1)).isFalse();
            assertThat(EventsTestUtils.count(events, PostDeleteEvent.class)).isEqualTo(1);
        }

        @Test
        void 원하는_포스트들을_제거하며_각각_댓글_제거_이벤트가_발행된다() {
            // when
            postService.delete(new DeletePostCommand(memberId, List.of(myPostId1, myPostId2)));

            // then
            assertThat(postServiceTestHelper.포스트_존재여부_확인(myPostId1)).isFalse();
            assertThat(postServiceTestHelper.포스트_존재여부_확인(myPostId2)).isFalse();
            assertThat(postServiceTestHelper.포스트_존재여부_확인(otherPostId)).isTrue();
            assertThat(EventsTestUtils.count(events, PostDeleteEvent.class)).isEqualTo(2);
        }
    }
}
