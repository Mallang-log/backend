package com.mallang.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.category.exception.NotFoundCategoryException;
import com.mallang.member.MemberServiceTestHelper;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.NoAuthorityUpdatePostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("포스트 서비스(PostService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
@SpringBootTest
class PostServiceTest {

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Autowired
    private CategoryServiceTestHelper categoryServiceTestHelper;

    @Autowired
    private PostService postService;

    private Long memberId;

    @BeforeEach
    void setUp() {
        memberId = memberServiceTestHelper.회원을_저장한다("말랑");
    }

    @Nested
    class 포스트_저장_시 {

        @Test
        void 카테고리_없는_포스트를_저장한다() {
            // given
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
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
            Long categoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, "Spring");
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .title("포스트 1")
                    .content("content")
                    .categoryId(categoryId)
                    .build();

            // when
            Long id = postService.create(command);

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(id);
            assertThat(post.getCategory().getName()).isEqualTo("Spring");
        }

        @Test
        void 없는_카테고리면_예외() {
            // given
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
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
        void 자신이_만든_카테고리가_아니면_예외() {
            // given
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("다른");
            Long categoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(otherMemberId, "Spring");
            CreatePostCommand command = CreatePostCommand.builder()
                    .memberId(memberId)
                    .title("포스트 1")
                    .content("content")
                    .categoryId(categoryId)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postService.create(command)
            ).isInstanceOf(NoAuthorityUseCategoryException.class);
        }
    }

    @Nested
    class 포스트_수정_시 {

        @Test
        void 내가_쓴_포스트를_수정할_수_있다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, "포스트", "내용");

            // when
            postService.update(new UpdatePostCommand(memberId, 포스트_ID, "수정제목", "수정내용", null));

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getContent()).isEqualTo("수정내용");
        }

        @Test
        void 다른_사람의_포스트은_수정할_수_없다() {
            // given
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("동훈");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, "포스트", "내용");

            // when
            assertThatThrownBy(() ->
                    postService.update(new UpdatePostCommand(otherMemberId, 포스트_ID, "수정제목", "수정내용", null))
            ).isInstanceOf(NoAuthorityUpdatePostException.class);

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
            assertThat(post.getTitle()).isEqualTo("포스트");
            assertThat(post.getContent()).isEqualTo("내용");
        }

        @Test
        void 포스트_수정_시_있던_카테고리릴_없앨_수_있다() {
            // given
            Long springCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, "Spring");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, "포스트", "내용", springCategoryId);

            // when
            postService.update(new UpdatePostCommand(memberId, 포스트_ID, "수정제목", "수정내용", null));

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getContent()).isEqualTo("수정내용");
            assertThat(post.getCategory()).isNull();
        }

        @Test
        void 포스트_수정_시_없던_카테고리를_설정할_수_있다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, "포스트", "내용", null);
            Long springCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, "Spring");

            // when
            postService.update(new UpdatePostCommand(memberId, 포스트_ID, "수정제목", "수정내용", springCategoryId));

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getContent()).isEqualTo("수정내용");
            assertThat(post.getCategory().getName()).isEqualTo("Spring");
        }

        @Test
        void 기존_카테고리를_다른_카테고리로_변경할_수_있다() {
            // given
            Long springCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, "Spring");
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, "포스트", "내용", springCategoryId);
            Long nodeCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, "Node");

            // when
            postService.update(new UpdatePostCommand(memberId, 포스트_ID, "수정제목", "수정내용", nodeCategoryId));

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getContent()).isEqualTo("수정내용");
            assertThat(post.getCategory().getName()).isEqualTo("Node");
        }

        @Test
        void 다른_사람의_카테고리거나_없는_카테고리로는_변경할_수_없다() {
            // given
            Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(memberId, "포스트", "내용", null);
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("other");
            Long otherMemberSpringCategoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(otherMemberId, "Spring");

            // when
            assertThatThrownBy(() ->
                    postService.update(new UpdatePostCommand(
                            memberId, 포스트_ID, "수정제목", "수정내용", 1000L
                    ))
            ).isInstanceOf(NotFoundCategoryException.class);
            assertThatThrownBy(() ->
                    postService.update(new UpdatePostCommand(
                            memberId, 포스트_ID, "수정제목", "수정내용", otherMemberSpringCategoryId
                    ))
            ).isInstanceOf(NoAuthorityUseCategoryException.class);

            // then
            Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
            assertThat(post.getTitle()).isEqualTo("포스트");
        }
    }
}
