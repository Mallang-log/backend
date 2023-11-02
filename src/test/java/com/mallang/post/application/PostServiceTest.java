package com.mallang.post.application;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.blog.domain.BlogName;
import com.mallang.blog.exception.IsNotBlogOwnerException;
import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.category.exception.NotFoundCategoryException;
import com.mallang.common.ServiceTest;
import com.mallang.common.TransactionHelper;
import com.mallang.member.MemberServiceTestHelper;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.Tag;
import com.mallang.post.exception.NoAuthorityUpdatePostException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    private Long memberId;
    private BlogName blogName;

    @Nested
    class 포스트_저장_시 {

        @BeforeEach
        void setUp() {
            memberId = memberServiceTestHelper.회원을_저장한다("말랑");
            blogName = blogServiceTestHelper.블로그_개설후_이름_반환(memberId, "mallang-log");
        }


        @Test
        void 카테고리_없는_포스트를_저장한다() {
            // given
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("포스트 1")
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
            Long categoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogName, "Spring");
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("포스트 1")
                    .content("content")
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
                    .blogName(blogName)
                    .title("포스트 1")
                    .content("content")
                    .categoryId(1000L)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postService.create(command)
            ).isInstanceOf(NotFoundCategoryException.class);
        }

        @Test
        void 다른_사람의_블로그에_글을_쓰려는_경우_예외() {
            // given
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("다른");
            BlogName otherBlogName = blogServiceTestHelper.블로그_개설후_이름_반환(otherMemberId, "other-log");

            // when
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(otherBlogName)
                    .title("포스트 1")
                    .content("content")
                    .build();

            // then
            assertThatThrownBy(() ->
                    postService.create(command)
            ).isInstanceOf(IsNotBlogOwnerException.class);
        }

        @Test
        void 자신이_만든_카테고리가_아니면_예외() {
            // given
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("다른");
            BlogName otherBlogName = blogServiceTestHelper.블로그_개설후_이름_반환(otherMemberId, "other-log");
            Long categoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(otherMemberId, otherBlogName, "Spring");
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("포스트 1")
                    .content("content")
                    .categoryId(categoryId)
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
                    .blogName(blogName)
                    .title("포스트 1")
                    .content("content")
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
            blogName = blogServiceTestHelper.블로그_개설후_이름_반환(memberId, "mallang-log");
        }

        @Test
        void 내가_쓴_포스트를_수정할_수_있다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용", "태그1");

            // when
            postService.update(new UpdatePostCommand(memberId, blogName, 포스트_ID, "수정제목", "수정내용", null, List.of("태그2")));

            // then
            transactionHelper.doAssert(() -> {
                Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
                assertThat(post.getTitle()).isEqualTo("수정제목");
                assertThat(post.getContent()).isEqualTo("수정내용");
                assertThat(post.getTags()).extracting(Tag::getContent)
                        .containsExactly("태그2");
            });
        }

        @Test
        void 다른_사람의_포스트는_수정할_수_없다() {
            // given
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("동훈");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용");

            // when
            assertThatThrownBy(() ->
                    postService.update(
                            new UpdatePostCommand(otherMemberId, blogName, 포스트_ID, "수정제목", "수정내용", null, emptyList()))
            ).isInstanceOf(NoAuthorityUpdatePostException.class);

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
            assertThat(post.getTitle()).isEqualTo("포스트");
            assertThat(post.getContent()).isEqualTo("내용");
        }

        @Test
        void 포스트_수정_시_있던_카테고리릴_없앨_수_있다() {
            // given
            Long springCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogName, "Spring");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용", springCategoryId);

            // when
            postService.update(new UpdatePostCommand(memberId, blogName, 포스트_ID, "수정제목", "수정내용", null, emptyList()));

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getContent()).isEqualTo("수정내용");
            assertThat(post.getCategory()).isNull();
        }

        @Test
        void 포스트_수정_시_없던_카테고리를_설정할_수_있다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용");
            Long springCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogName, "Spring");

            // when
            postService.update(
                    new UpdatePostCommand(memberId, blogName, 포스트_ID, "수정제목", "수정내용", springCategoryId, emptyList()));

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
            Long springCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogName, "Spring");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용", springCategoryId);
            Long nodeCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogName, "Node");

            // when
            postService.update(new UpdatePostCommand(
                    memberId,
                    blogName,
                    포스트_ID,
                    "수정제목",
                    "수정내용",
                    nodeCategoryId,
                    emptyList())
            );

            // then
            transactionHelper.doAssert(() -> {
                Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
                assertThat(post.getTitle()).isEqualTo("수정제목");
                assertThat(post.getContent()).isEqualTo("수정내용");
                assertThat(post.getCategory().getName()).isEqualTo("Node");
            });
        }

        @Test
        void 다른_사람의_카테고리거나_없는_카테고리로는_변경할_수_없다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용");
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("other");
            BlogName otherBlogName = blogServiceTestHelper.블로그_개설후_이름_반환(otherMemberId, "other-log");
            Long otherMemberSpringCategoryId =
                    categoryServiceTestHelper.최상위_카테고리를_저장한다(otherMemberId, otherBlogName, "Spring");

            // when
            assertThatThrownBy(() ->
                    postService.update(new UpdatePostCommand(
                            memberId, blogName, 포스트_ID, "수정제목", "수정내용", 1000L, emptyList()
                    ))
            ).isInstanceOf(NotFoundCategoryException.class);
            assertThatThrownBy(() ->
                    postService.update(new UpdatePostCommand(
                            memberId, otherBlogName, 포스트_ID, "수정제목", "수정내용", otherMemberSpringCategoryId, emptyList()
                    ))
            ).isInstanceOf(NoAuthorityUseCategoryException.class);

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
            assertThat(post.getTitle()).isEqualTo("포스트");
        }
    }
}
