package com.mallang.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.category.CategoryHierarchyViolationException;
import com.mallang.category.ChildCategoryExistException;
import com.mallang.category.DuplicateCategoryNameException;
import com.mallang.common.EventsTestUtils;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CreatePostCategoryCommand;
import com.mallang.post.application.command.DeletePostCategoryCommand;
import com.mallang.post.application.command.UpdatePostCategoryHierarchyCommand;
import com.mallang.post.application.command.UpdatePostCategoryNameCommand;
import com.mallang.post.domain.category.PostCategory;
import com.mallang.post.domain.category.PostCategoryDeletedEvent;
import com.mallang.post.exception.NoAuthorityPostCategoryException;
import com.mallang.post.exception.NotFoundPostCategoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("카테고리 서비스 (PostCategoryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostCategoryServiceTest extends ServiceTest {

    private Long mallangId;
    private String mallangBlogName;
    private Long otherMemberId;
    private String otherBlogName;

    @BeforeEach
    void setUp() {
        mallangId = 회원을_저장한다("mallang");
        mallangBlogName = 블로그_개설(mallangId, "mallang-log");
        otherMemberId = 회원을_저장한다("동훈");
        otherBlogName = 블로그_개설(otherMemberId, "donghun");
    }

    @Nested
    class 저장_시 {

        @Test
        void 최상위_카테고리로_저장할_수_있다() {
            // given
            CreatePostCategoryCommand command = CreatePostCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogName(mallangBlogName)
                    .name("최상위 카테고리")
                    .parentId(null)
                    .build();

            // when
            Long 최상위_카테고리 = postCategoryService.create(command);

            // then
            PostCategory postCategory = postCategoryRepository.getById(최상위_카테고리);
            assertThat(postCategory.getParent()).isNull();
            assertThat(postCategory.getName()).isEqualTo("최상위 카테고리");
        }

        @Test
        void 계층형으로_저장할_수_있다() {
            // given
            Long 최상위 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            CreatePostCategoryCommand command = CreatePostCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogName(mallangBlogName)
                    .name("하위 카테고리")
                    .parentId(최상위)
                    .build();

            // when
            Long id = postCategoryService.create(command);

            // then
            PostCategory postCategory = postCategoryRepository.getById(id);
            assertThat(postCategory.getParent().getId()).isEqualTo(최상위);
            assertThat(postCategory.getName()).isEqualTo("하위 카테고리");
        }

        @Test
        void 없는_부모_카테고리_ID를_설정한_경우_예외() {
            // given
            CreatePostCategoryCommand command = CreatePostCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogName(mallangBlogName)
                    .name("하위 카테고리")
                    .parentId(100L)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.create(command)
            ).isInstanceOf(NotFoundPostCategoryException.class);
        }

        @Test
        void 하위_카테고리를_생성하려는_회원가_상위_카테고리를_생성한_회원이_동일하지_않으면_예외() {
            // given
            Long 최상위 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            CreatePostCategoryCommand command = CreatePostCategoryCommand.builder()
                    .memberId(otherMemberId)
                    .blogName(otherBlogName)
                    .name("하위 카테고리")
                    .parentId(최상위)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.create(command)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
        }

        @Test
        void 루트끼리는_이름이_같을_수_없다() {
            // given
            Long rootId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            CreatePostCategoryCommand command = CreatePostCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogName(mallangBlogName)
                    .name("최상위")
                    .parentId(null)
                    .prevId(rootId)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.create(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 형제끼리는_이름이_같을_수_없다() {
            // given
            Long rootId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childI1 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "자식",
                    rootId,
                    null,
                    null
            ));
            CreatePostCategoryCommand command = CreatePostCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogName(mallangBlogName)
                    .name("자식")
                    .parentId(rootId)
                    .prevId(childI1)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.create(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 이름_수정_시 {

        @Test
        void 자신의_카테고리라면_수정_가능() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            UpdatePostCategoryNameCommand command = UpdatePostCategoryNameCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .name("수정")
                    .build();

            // when
            postCategoryService.updateName(command);

            // then
            PostCategory postCategory = postCategoryRepository.getById(categoryId);
            assertThat(postCategory.getName()).isEqualTo("수정");
        }

        @Test
        void 같은_부모를_가진_직계_자식끼리는_이름이_겹쳐서는_안된다() {
            // given
            Long 최상위 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long 자식1 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위1",
                    최상위,
                    null,
                    null
            ));
            Long 자식2 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위2",
                    최상위,
                    자식1,
                    null
            ));
            UpdatePostCategoryNameCommand command = UpdatePostCategoryNameCommand.builder()
                    .categoryId(자식2)
                    .memberId(mallangId)
                    .name("하위1")
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateName(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 루트끼리는_이름이_같을_수_없다() {
            // given
            Long 최상위 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long 최상위2 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위2",
                    null,
                    최상위,
                    null
            ));
            UpdatePostCategoryNameCommand command = UpdatePostCategoryNameCommand.builder()
                    .categoryId(최상위2)
                    .memberId(mallangId)
                    .name("최상위")
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateName(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 계층_구조_수정_시 {

        @Test
        void 자신의_카테고리라면_수정_가능() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            UpdatePostCategoryHierarchyCommand command = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(childCategoryId)
                    .memberId(mallangId)
                    .parentId(null)
                    .nextId(categoryId)
                    .build();

            // when
            postCategoryService.updateHierarchy(command);

            // then
            PostCategory postCategory = postCategoryRepository.getById(childCategoryId);
            assertThat(postCategory.getNextSibling().getId()).isEqualTo(categoryId);
            assertThat(postCategory.getPreviousSibling()).isNull();
        }

        @Test
        void 부모_카테고리를_제거함으로써_최상위_카테고리로_만들_수_있다() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            UpdatePostCategoryHierarchyCommand command = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(childCategoryId)
                    .memberId(mallangId)
                    .parentId(null)
                    .prevId(categoryId)
                    .build();

            // when
            postCategoryService.updateHierarchy(command);

            // then
            PostCategory postCategory = postCategoryRepository.getById(childCategoryId);
            assertThat(postCategory.getName()).isEqualTo("하위");
            assertThat(postCategory.getParent()).isNull();
        }

        // Lazy Loading 으로 인해 오동작하는 문제가 있어서 체크를 위해 필요
        @Test
        void 부모_카테고리를_변경할_수_있다() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            Long childChildCategoryId1 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "더하위",
                    childCategoryId,
                    null,
                    null
            ));
            UpdatePostCategoryHierarchyCommand command = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(childChildCategoryId1)
                    .memberId(mallangId)
                    .parentId(categoryId)
                    .nextId(childCategoryId)
                    .build();

            // when
            postCategoryService.updateHierarchy(command);

            // then
            transactionHelper.doAssert(() -> {
                PostCategory postCategory = postCategoryRepository.getById(categoryId);
                assertThat(postCategory.getSortedChildren())
                        .extracting(PostCategory::getName)
                        .containsExactly("더하위", "하위");
            });
        }

        // Lazy Loading 으로 인해 오동작하는 문제가 있어서 체크를 위해 필요
        @Test
        void 부모_카테고리를_변경할_수_있다2() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            Long childChildCategoryId1 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "더하위",
                    childCategoryId,
                    null,
                    null
            ));
            Long childChildChildCategoryId1 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "더더하위",
                    childChildCategoryId1,
                    null,
                    null
            ));
            UpdatePostCategoryHierarchyCommand command = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(childChildChildCategoryId1)
                    .memberId(mallangId)
                    .parentId(categoryId)
                    .nextId(childCategoryId)
                    .build();

            // when
            postCategoryService.updateHierarchy(command);

            // then
            transactionHelper.doAssert(() -> {
                PostCategory postCategory = postCategoryRepository.getById(categoryId);
                assertThat(postCategory.getSortedChildren())
                        .extracting(PostCategory::getName)
                        .containsExactly("더더하위", "하위");
            });
        }

        // Lazy Loading 으로 인해 오동작하는 문제가 있어서 체크를 위해 필요
        @Test
        void 부모_카테고리를_변경할_수_있다3() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            Long childNextCategoryId1 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위 옆",
                    categoryId,
                    childCategoryId,
                    null
            ));
            UpdatePostCategoryHierarchyCommand command = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(childNextCategoryId1)
                    .memberId(mallangId)
                    .parentId(childCategoryId)
                    .build();

            // when
            postCategoryService.updateHierarchy(command);

            // then
            transactionHelper.doAssert(() -> {
                PostCategory postCategory = postCategoryRepository.getById(categoryId);
                assertThat(postCategory.getSortedChildren())
                        .extracting(PostCategory::getName)
                        .containsExactly("하위");
            });
            transactionHelper.doAssert(() -> {
                PostCategory postCategory = postCategoryRepository.getById(childCategoryId);
                assertThat(postCategory.getSortedChildren())
                        .extracting(PostCategory::getName)
                        .containsExactly("하위 옆");
            });
        }

        // Lazy Loading 으로 인해 오동작하는 문제가 있어서 체크를 위해 필요
        @Test
        void 부모_카테고리를_변경할_수_있다4() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            Long childNextCategoryId1 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위 옆",
                    categoryId,
                    null,
                    childCategoryId
            ));
            UpdatePostCategoryHierarchyCommand command = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(childNextCategoryId1)
                    .memberId(mallangId)
                    .parentId(childCategoryId)
                    .build();

            // when
            postCategoryService.updateHierarchy(command);

            // then
            transactionHelper.doAssert(() -> {
                PostCategory postCategory = postCategoryRepository.getById(categoryId);
                assertThat(postCategory.getSortedChildren())
                        .extracting(PostCategory::getName)
                        .containsExactly("하위");
            });
            transactionHelper.doAssert(() -> {
                PostCategory postCategory = postCategoryRepository.getById(childCategoryId);
                assertThat(postCategory.getSortedChildren())
                        .extracting(PostCategory::getName)
                        .containsExactly("하위 옆");
            });
        }

        @Test
        void 자신_혹은_자신의_하위_카테고리를_자신의_부모로_만드려는_경우_예외() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            Long childChildCategoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "더하위1",
                    childCategoryId,
                    null,
                    null
            ));
            UpdatePostCategoryHierarchyCommand selfParent = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .parentId(categoryId)
                    .build();
            UpdatePostCategoryHierarchyCommand childToParent = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .parentId(childChildCategoryId)
                    .build();
            UpdatePostCategoryHierarchyCommand descendantToParent = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .parentId(childChildCategoryId)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(selfParent)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(childToParent)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(descendantToParent)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 자신의_카테고리가_아니면_예외() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            UpdatePostCategoryHierarchyCommand command = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(categoryId)
                    .memberId(otherMemberId)
                    .parentId(null)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
        }

        @Test
        void 다른_사람의_카테고리의_하위_카테고리로_변경하려는_경우_예외() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long otherCategory = postCategoryService.create(new CreatePostCategoryCommand(
                    otherMemberId,
                    otherBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            UpdatePostCategoryHierarchyCommand command = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .parentId(otherCategory)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
        }

        @Test
        void 같은_부모를_가진_형제끼리_이름이_겹치면_예외() {
            // given
            Long 최상위 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long 최상위2 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "자식1",
                    null,
                    최상위,
                    null
            ));
            Long 자식1 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "자식1",
                    최상위,
                    null,
                    null
            ));
            UpdatePostCategoryHierarchyCommand command = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(최상위2)
                    .memberId(mallangId)
                    .parentId(최상위)
                    .prevId(자식1)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 루트끼리_이름이_같으면_예외() {
            // given
            Long 최상위 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long 자식 = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    최상위,
                    null,
                    null
            ));
            UpdatePostCategoryHierarchyCommand command = UpdatePostCategoryHierarchyCommand.builder()
                    .categoryId(자식)
                    .memberId(mallangId)
                    .parentId(null)
                    .prevId(최상위)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postCategoryService.updateHierarchy(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 제거_시 {

        @Test
        void 하위_카테고리가_있다면_오류() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            DeletePostCategoryCommand command = DeletePostCategoryCommand.builder()
                    .memberId(mallangId)
                    .categoryId(categoryId)
                    .build();

            // when
            assertThatThrownBy(() ->
                    postCategoryService.delete(command)
            ).isInstanceOf(ChildCategoryExistException.class);

            // then
            assertThat(postCategoryRepository.getById(categoryId)).isNotNull();
        }

        @Test
        void 자신의_카테고리가_아니라면_예외() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            DeletePostCategoryCommand command = DeletePostCategoryCommand.builder()
                    .memberId(otherMemberId)
                    .categoryId(categoryId)
                    .build();
            // when
            assertThatThrownBy(() ->
                    postCategoryService.delete(command)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);

            // then
            assertThat(postCategoryRepository.getById(categoryId)).isNotNull();
        }

        @Test
        void 부모_카테고리의_자식에서_제거된다() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            DeletePostCategoryCommand command = DeletePostCategoryCommand.builder()
                    .memberId(mallangId)
                    .categoryId(childCategoryId)
                    .build();
            // when
            postCategoryService.delete(command);

            // then
            assertThatThrownBy(() ->
                    postCategoryRepository.getById(childCategoryId)
            ).isInstanceOf(NotFoundPostCategoryException.class);
            transactionHelper.doAssert(() ->
                    assertThat(postCategoryRepository.getById(categoryId).getSortedChildren()).isEmpty()
            );
        }

        @Test
        void 카테고리_제거_이벤트가_발행된다() {
            // given
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            DeletePostCategoryCommand command = DeletePostCategoryCommand.builder()
                    .memberId(mallangId)
                    .categoryId(categoryId)
                    .build();

            // when
            postCategoryService.delete(command);

            // then
            int count = EventsTestUtils.count(events, PostCategoryDeletedEvent.class);
            assertThat(count).isEqualTo(1);
        }
    }
}
