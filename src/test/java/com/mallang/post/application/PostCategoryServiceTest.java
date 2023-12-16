package com.mallang.post.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.auth.OauthMemberFixture.깃허브_회원;
import static com.mallang.blog.BlogFixture.blog;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.post.PostCategoryFixture.postCategory;
import static com.mallang.post.PostFixture.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.category.CategoryHierarchyViolationException;
import com.mallang.category.ChildCategoryExistException;
import com.mallang.category.DuplicateCategoryNameException;
import com.mallang.post.application.command.CreatePostCategoryCommand;
import com.mallang.post.application.command.DeletePostCategoryCommand;
import com.mallang.post.application.command.UpdatePostCategoryHierarchyCommand;
import com.mallang.post.application.command.UpdatePostCategoryNameCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostCategory;
import com.mallang.post.domain.PostCategoryRepository;
import com.mallang.post.domain.PostCategoryValidator;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.exception.NoAuthorityPostCategoryException;
import com.mallang.post.exception.NotFoundPostCategoryException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("포스트 카테고리 서비스 (PostCategoryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostCategoryServiceTest {

    private final BlogRepository blogRepository = mock(BlogRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final PostCategoryRepository postCategoryRepository = mock(PostCategoryRepository.class);
    private final PostCategoryValidator postCategoryValidator = mock(PostCategoryValidator.class);
    private final PostCategoryService postCategoryService = new PostCategoryService(
            blogRepository,
            postRepository,
            memberRepository,
            postCategoryRepository,
            postCategoryValidator
    );

    private final Member mallang = 깃허브_말랑(1L);
    private final Member other = 깃허브_회원(2L, "other");
    private final Blog mallangBlog = mallangBlog(1L, mallang);
    private final Blog otherBlog = blog(2L, "other", other);

    @BeforeEach
    void setUp() {
        given(memberRepository.getById(mallang.getId())).willReturn(mallang);
        given(memberRepository.getById(other.getId())).willReturn(other);
        given(blogRepository.getByName(mallangBlog.getName())).willReturn(mallangBlog);
        given(blogRepository.getByName(otherBlog.getName())).willReturn(otherBlog);
        given(postCategoryRepository.getByIdIfIdNotNull(null))
                .willReturn(null);
        given(postCategoryRepository.save(any()))
                .willReturn(mock(PostCategory.class));
    }

    @Nested
    class 저장_시 {

        @Test
        void 카테고리를_저장한다() {
            // given
            var command = new CreatePostCategoryCommand(
                    mallang.getId(),
                    mallangBlog.getName(),
                    "최상위 카테고리",
                    null,
                    null,
                    null
            );

            // when
            Long id = postCategoryService.create(command);

            // then
            then(postCategoryRepository)
                    .should(times(1))
                    .save(any());
        }

        @Test
        void 이미_카테고리가_존재하는데_이와의_관계를_명시하지_않으면_예외() {
            // given
            willThrow(CategoryHierarchyViolationException.class)
                    .given(postCategoryValidator)
                    .validateNoCategories(any());
            var command = new CreatePostCategoryCommand(
                    mallang.getId(),
                    mallangBlog.getName(),
                    "최상위 카테고리",
                    null,
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                postCategoryService.create(command);
            }).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 계층형으로_저장할_수_있다() {
            // given
            var parent = postCategory(1L, "root", mallangBlog);
            var prev = postCategory(2L, "prev", mallangBlog);
            var next = postCategory(3L, "next", mallangBlog);
            prev.updateHierarchy(parent, null, null);
            next.updateHierarchy(parent, prev, null);
            given(postCategoryRepository.getByIdIfIdNotNull(parent.getId())).willReturn(parent);
            given(postCategoryRepository.getByIdIfIdNotNull(prev.getId())).willReturn(prev);
            given(postCategoryRepository.getByIdIfIdNotNull(next.getId())).willReturn(next);
            var command = new CreatePostCategoryCommand(
                    mallang.getId(),
                    mallangBlog.getName(),
                    "prev 와 next 차이 parent 자식",
                    parent.getId(),
                    prev.getId(),
                    next.getId()
            );

            // when
            Long id = postCategoryService.create(command);

            // then
            then(postCategoryRepository)
                    .should(times(1))
                    .save(any());
            assertThat(parent.getChildren()).hasSize(3);
            assertThat(prev.getNextSibling()).isNotEqualTo(next);
            assertThat(next.getPreviousSibling()).isNotEqualTo(prev);
            PostCategory saved = prev.getNextSibling();
            assertThat(saved).isEqualTo(next.getPreviousSibling());
            assertThat(saved.getParent()).isEqualTo(parent);
        }

        @Test
        void 없는_부모나_형제_카테고리_ID를_설정한_경우_예외() {
            // given
            given(postCategoryRepository.getByIdIfIdNotNull(100L))
                    .willThrow(NotFoundPostCategoryException.class);
            var command1 = new CreatePostCategoryCommand(
                    mallang.getId(),
                    mallangBlog.getName(),
                    "name",
                    100L,
                    null,
                    null
            );
            var command2 = new CreatePostCategoryCommand(
                    mallang.getId(),
                    mallangBlog.getName(),
                    "name",
                    100L,
                    null,
                    null
            );
            var command3 = new CreatePostCategoryCommand(
                    mallang.getId(),
                    mallangBlog.getName(),
                    "name",
                    100L,
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.create(command1)
            ).isInstanceOf(NotFoundPostCategoryException.class);
            assertThatThrownBy(() ->
                    postCategoryService.create(command2)
            ).isInstanceOf(NotFoundPostCategoryException.class);
            assertThatThrownBy(() ->
                    postCategoryService.create(command3)
            ).isInstanceOf(NotFoundPostCategoryException.class);
        }

        @Test
        void 다른_사람의_하위_카테고리로_생성되려는_경우_예외() {
            // given
            var parent = postCategory(1L, "root", otherBlog);
            given(postCategoryRepository.getByIdIfIdNotNull(parent.getId())).willReturn(parent);
            var command = new CreatePostCategoryCommand(
                    mallang.getId(),
                    mallangBlog.getName(),
                    "name",
                    parent.getId(),
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.create(command)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
        }

        @Test
        void 다른_사람의_형제_카테고리로_생성되려는_경우_예외() {
            // given
            var prev = postCategory(1L, "prev", otherBlog);
            given(postCategoryRepository.getByIdIfIdNotNull(prev.getId())).willReturn(prev);
            var command1 = new CreatePostCategoryCommand(
                    mallang.getId(),
                    mallangBlog.getName(),
                    "name",
                    null,
                    prev.getId(),
                    null
            );
            var command2 = new CreatePostCategoryCommand(
                    mallang.getId(),
                    mallangBlog.getName(),
                    "name",
                    null,
                    null,
                    prev.getId()
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.create(command1)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
            assertThatThrownBy(() ->
                    postCategoryService.create(command2)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
        }

        @Test
        void 형제끼리는_이름이_같을_수_없다() {
            // given
            var parent = postCategory(1L, "same name", mallangBlog);
            given(postCategoryRepository.getByIdIfIdNotNull(parent.getId())).willReturn(parent);
            var command1 = new CreatePostCategoryCommand(
                    mallang.getId(),
                    mallangBlog.getName(),
                    "same name",
                    null,
                    parent.getId(),
                    null
            );
            var command2 = new CreatePostCategoryCommand(
                    mallang.getId(),
                    mallangBlog.getName(),
                    "same name",
                    null,
                    null,
                    parent.getId()
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.create(command1)
            ).isInstanceOf(DuplicateCategoryNameException.class);
            assertThatThrownBy(() ->
                    postCategoryService.create(command2)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 이름_수정_시 {

        private final PostCategory postCategory = new PostCategory("spring", mallang, mallangBlog);

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(postCategory, "id", 1L);
            given(postCategoryRepository.getById(postCategory.getId())).willReturn(postCategory);
        }

        @Test
        void 다른_사람의_카테고리는_수정할_수_없단() {
            // given
            var command = new UpdatePostCategoryNameCommand(
                    postCategory.getId(),
                    other.getId(),
                    "수정"
            );

            // when & then
            assertThatThrownBy(() -> {
                postCategoryService.updateName(command);
            }).isInstanceOf(NoAuthorityPostCategoryException.class);
        }

        @Test
        void 자신의_카테고리라면_수정_가능() {
            // given
            var command = new UpdatePostCategoryNameCommand(
                    postCategory.getId(),
                    mallang.getId(),
                    "수정"
            );

            // when
            postCategoryService.updateName(command);

            // then
            assertThat(postCategory.getName()).isEqualTo("수정");
        }

        @Test
        void 형제끼리는_이름이_같을_수_없다() {
            // given
            PostCategory next = new PostCategory("next", mallang, mallangBlog);
            next.updateHierarchy(null, postCategory, null);
            var command = new UpdatePostCategoryNameCommand(
                    postCategory.getId(),
                    mallang.getId(),
                    "next"
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateName(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 계층_구조_수정_시 {

        private final PostCategory parent = postCategory(1L, "parent", mallangBlog);
        private final PostCategory prev = postCategory(2L, "prev", mallangBlog);
        private final PostCategory next = postCategory(3L, "next", mallangBlog);

        @BeforeEach
        void setUp() {
            prev.updateHierarchy(parent, null, null);
            next.updateHierarchy(parent, prev, null);
            given(postCategoryRepository.getById(parent.getId())).willReturn(parent);
            given(postCategoryRepository.getById(prev.getId())).willReturn(prev);
            given(postCategoryRepository.getById(next.getId())).willReturn(next);
            given(postCategoryRepository.getByIdIfIdNotNull(parent.getId())).willReturn(parent);
            given(postCategoryRepository.getByIdIfIdNotNull(prev.getId())).willReturn(prev);
            given(postCategoryRepository.getByIdIfIdNotNull(next.getId())).willReturn(next);
            given(postCategoryRepository.getByIdIfIdNotNull(null)).willReturn(null);
        }

        @Test
        void 자신의_카테고리라면_수정_가능() {
            // given
            var command = new UpdatePostCategoryHierarchyCommand(
                    prev.getId(),
                    mallang.getId(),
                    next.getId(),
                    null,
                    null
            );

            // when
            postCategoryService.updateHierarchy(command);

            // then
            assertThat(prev.getParent()).isEqualTo(next);
            assertThat(prev.getNextSibling()).isNull();
            assertThat(next.getPreviousSibling()).isNull();
            assertThat(next.getChildren())
                    .containsExactly(prev);
        }

        @Test
        void 자신_혹은_자신의_하위_카테고리를_자신의_부모로_만드려는_경우_예외() {
            // given
            var command = new UpdatePostCategoryHierarchyCommand(
                    parent.getId(),
                    mallang.getId(),
                    next.getId(),
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(command)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 자신의_카테고리가_아니면_예외() {
            // given
            var command = new UpdatePostCategoryHierarchyCommand(
                    prev.getId(),
                    other.getId(),
                    next.getId(),
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
        }

        @Test
        void 다른_사람의_카테고리의_하위_카테고리로_변경하려는_경우_예외() {
            // given
            PostCategory otherCategory = postCategory(4L, "other", otherBlog);
            given(postCategoryRepository.getByIdIfIdNotNull(otherCategory.getId()))
                    .willReturn(otherCategory);
            var command = new UpdatePostCategoryHierarchyCommand(
                    prev.getId(),
                    other.getId(),
                    otherCategory.getId(),
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
        }

        @Test
        void 다른_사람의_카테고리의_형제_카테고리로_변경하려는_경우_예외() {
            // given
            PostCategory otherCategory = postCategory(4L, "other", otherBlog);
            given(postCategoryRepository.getByIdIfIdNotNull(otherCategory.getId()))
                    .willReturn(otherCategory);
            var command = new UpdatePostCategoryHierarchyCommand(
                    prev.getId(),
                    other.getId(),
                    null,
                    otherCategory.getId(),
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
        }

        @Test
        void 변경_시_이름이_겹치는_형제가_있으면_예외() {
            // given
            PostCategory postCategory = postCategory(5L, "parent", mallangBlog);
            postCategory.updateHierarchy(parent, next, null);
            given(postCategoryRepository.getById(postCategory.getId())).willReturn(postCategory);
            var command = new UpdatePostCategoryHierarchyCommand(
                    postCategory.getId(),
                    mallang.getId(),
                    null,
                    parent.getId(),
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 제거_시 {

        private final PostCategory parent = postCategory(1L, "parent", mallangBlog);
        private final PostCategory prev = postCategory(2L, "prev", mallangBlog);
        private final PostCategory next = postCategory(3L, "next", mallangBlog);

        @BeforeEach
        void setUp() {
            prev.updateHierarchy(parent, null, null);
            next.updateHierarchy(parent, prev, null);
            given(postCategoryRepository.getById(parent.getId())).willReturn(parent);
            given(postCategoryRepository.getById(prev.getId())).willReturn(prev);
            given(postCategoryRepository.getById(next.getId())).willReturn(next);
            given(postCategoryRepository.getByIdIfIdNotNull(parent.getId())).willReturn(parent);
            given(postCategoryRepository.getByIdIfIdNotNull(prev.getId())).willReturn(prev);
            given(postCategoryRepository.getByIdIfIdNotNull(next.getId())).willReturn(next);
            given(postCategoryRepository.getByIdIfIdNotNull(null)).willReturn(null);
        }

        @Test
        void 하위_카테고리가_있다면_예외() {
            // given
            var command = new DeletePostCategoryCommand(
                    mallang.getId(),
                    parent.getId()
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.delete(command)
            ).isInstanceOf(ChildCategoryExistException.class);
        }

        @Test
        void 자신의_카테고리가_아니라면_예외() {
            // given
            var command = new DeletePostCategoryCommand(
                    other.getId(),
                    prev.getId()
            );

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.delete(command)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
        }

        @Test
        void 부모_카테고리의_자식에서_제거된다() {
            // given
            var command = new DeletePostCategoryCommand(
                    mallang.getId(),
                    prev.getId()
            );

            // when
            postCategoryService.delete(command);

            // then
            then(postCategoryRepository)
                    .should(times(1))
                    .delete(prev);
            assertThat(parent.getChildren())
                    .doesNotContain(prev);
            assertThat(next.getPreviousSibling()).isNull();
        }

        @Test
        void 이전_카테고리와_다음_카테고리는_이어진다() {
            // given
            PostCategory last = postCategory(5L, "last", mallangBlog);
            last.updateHierarchy(parent, next, null);
            var command = new DeletePostCategoryCommand(
                    mallang.getId(),
                    next.getId()
            );

            // when
            postCategoryService.delete(command);

            // then
            then(postCategoryRepository)
                    .should(times(1))
                    .delete(next);
            assertThat(prev.getNextSibling()).isEqualTo(last);
            assertThat(last.getPreviousSibling()).isEqualTo(prev);
            assertThat(next.getNextSibling()).isNull();
            assertThat(next.getPreviousSibling()).isNull();
            assertThat(parent.getChildren())
                    .doesNotContain(next);
        }

        @Test
        void 해당_카테고리에_속한_포스트들을_카테고리_없음으로_만든다() {
            // given
            Post post1 = post(mallangBlog, prev);
            Post post2 = post(mallangBlog, prev);
            given(postRepository.findAllByCategory(prev))
                    .willReturn(List.of(post1, post2));
            var command = new DeletePostCategoryCommand(
                    mallang.getId(),
                    prev.getId()
            );

            // when
            postCategoryService.delete(command);

            // then
            then(postCategoryRepository)
                    .should(times(1))
                    .delete(prev);
            assertThat(post1.getCategory()).isNull();
            assertThat(post2.getCategory()).isNull();
        }
    }
}
