package com.mallang.category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.category.application.command.DeleteCategoryCommand;
import com.mallang.category.application.command.UpdateCategoryHierarchyCommand;
import com.mallang.category.application.command.UpdateCategoryNameCommand;
import com.mallang.category.domain.Category;
import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.ChildCategoryExistException;
import com.mallang.category.exception.DuplicateCategoryNameException;
import com.mallang.category.exception.NoAuthorityCategoryException;
import com.mallang.category.exception.NotFoundCategoryException;
import com.mallang.common.EventsTestUtils;
import com.mallang.common.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("카테고리 서비스 (CategoryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CategoryServiceTest extends ServiceTest {

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
            CreateCategoryCommand command = CreateCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogName(mallangBlogName)
                    .name("최상위 카테고리")
                    .parentId(null)
                    .build();

            // when
            Long 최상위_카테고리 = categoryService.create(command);

            // then
            Category category = categoryRepository.getById(최상위_카테고리);
            assertThat(category.getParent()).isNull();
            assertThat(category.getName()).isEqualTo("최상위 카테고리");
        }

        @Test
        void 계층형으로_저장할_수_있다() {
            // given
            Long 최상위 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            CreateCategoryCommand command = CreateCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogName(mallangBlogName)
                    .name("하위 카테고리")
                    .parentId(최상위)
                    .build();

            // when
            Long id = categoryService.create(command);

            // then
            Category category = categoryRepository.getById(id);
            assertThat(category.getParent().getId()).isEqualTo(최상위);
            assertThat(category.getName()).isEqualTo("하위 카테고리");
        }

        @Test
        void 없는_부모_카테고리_ID를_설정한_경우_예외() {
            // given
            CreateCategoryCommand command = CreateCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogName(mallangBlogName)
                    .name("하위 카테고리")
                    .parentId(100L)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.create(command)
            ).isInstanceOf(NotFoundCategoryException.class);
        }

        @Test
        void 하위_카테고리를_생성하려는_회원가_상위_카테고리를_생성한_회원이_동일하지_않으면_예외() {
            // given
            Long 최상위 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            CreateCategoryCommand command = CreateCategoryCommand.builder()
                    .memberId(otherMemberId)
                    .blogName(otherBlogName)
                    .name("하위 카테고리")
                    .parentId(최상위)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.create(command)
            ).isInstanceOf(NoAuthorityCategoryException.class);
        }

        @Test
        void 루트끼리는_이름이_같을_수_없다() {
            // given
            Long rootId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            CreateCategoryCommand command = CreateCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogName(mallangBlogName)
                    .name("최상위")
                    .parentId(null)
                    .prevId(rootId)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.create(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 형제끼리는_이름이_같을_수_없다() {
            // given
            Long rootId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childI1 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "자식",
                    rootId,
                    null,
                    null
            ));
            CreateCategoryCommand command = CreateCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogName(mallangBlogName)
                    .name("자식")
                    .parentId(rootId)
                    .prevId(childI1)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.create(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 이름_수정_시 {

        @Test
        void 자신의_카테고리라면_수정_가능() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            UpdateCategoryNameCommand command = UpdateCategoryNameCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .name("수정")
                    .build();

            // when
            categoryService.updateName(command);

            // then
            Category category = categoryRepository.getById(categoryId);
            assertThat(category.getName()).isEqualTo("수정");
        }

        @Test
        void 같은_부모를_가진_직계_자식끼리는_이름이_겹쳐서는_안된다() {
            // given
            Long 최상위 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long 자식1 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위1",
                    최상위,
                    null,
                    null
            ));
            Long 자식2 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위2",
                    최상위,
                    자식1,
                    null
            ));
            UpdateCategoryNameCommand command = UpdateCategoryNameCommand.builder()
                    .categoryId(자식2)
                    .memberId(mallangId)
                    .name("하위1")
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.updateName(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 루트끼리는_이름이_같을_수_없다() {
            // given
            Long 최상위 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long 최상위2 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위2",
                    null,
                    최상위,
                    null
            ));
            UpdateCategoryNameCommand command = UpdateCategoryNameCommand.builder()
                    .categoryId(최상위2)
                    .memberId(mallangId)
                    .name("최상위")
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.updateName(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 계층_구조_수정_시 {

        @Test
        void 자신의_카테고리라면_수정_가능() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            UpdateCategoryHierarchyCommand command = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(childCategoryId)
                    .memberId(mallangId)
                    .parentId(null)
                    .nextId(categoryId)
                    .build();

            // when
            categoryService.updateHierarchy(command);

            // then
            Category category = categoryRepository.getById(childCategoryId);
            assertThat(category.getNextSibling().getId()).isEqualTo(categoryId);
            assertThat(category.getPreviousSibling()).isNull();
        }

        @Test
        void 부모_카테고리를_제거함으로써_최상위_카테고리로_만들_수_있다() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            UpdateCategoryHierarchyCommand command = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(childCategoryId)
                    .memberId(mallangId)
                    .parentId(null)
                    .prevId(categoryId)
                    .build();

            // when
            categoryService.updateHierarchy(command);

            // then
            Category category = categoryRepository.getById(childCategoryId);
            assertThat(category.getName()).isEqualTo("하위");
            assertThat(category.getParent()).isNull();
        }

        // Lazy Loading 으로 인해 오동작하는 문제가 있어서 체크를 위해 필요
        @Test
        void 부모_카테고리를_변경할_수_있다() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            Long childChildCategoryId1 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "더하위",
                    childCategoryId,
                    null,
                    null
            ));
            UpdateCategoryHierarchyCommand command = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(childChildCategoryId1)
                    .memberId(mallangId)
                    .parentId(categoryId)
                    .nextId(childCategoryId)
                    .build();

            // when
            categoryService.updateHierarchy(command);

            // then
            transactionHelper.doAssert(() -> {
                Category category = categoryRepository.getById(categoryId);
                assertThat(category.getSortedChildren())
                        .extracting(Category::getName)
                        .containsExactly("더하위", "하위");
            });
        }

        // Lazy Loading 으로 인해 오동작하는 문제가 있어서 체크를 위해 필요
        @Test
        void 부모_카테고리를_변경할_수_있다2() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            Long childChildCategoryId1 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "더하위",
                    childCategoryId,
                    null,
                    null
            ));
            Long childChildChildCategoryId1 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "더더하위",
                    childChildCategoryId1,
                    null,
                    null
            ));
            UpdateCategoryHierarchyCommand command = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(childChildChildCategoryId1)
                    .memberId(mallangId)
                    .parentId(categoryId)
                    .nextId(childCategoryId)
                    .build();

            // when
            categoryService.updateHierarchy(command);

            // then
            transactionHelper.doAssert(() -> {
                Category category = categoryRepository.getById(categoryId);
                assertThat(category.getSortedChildren())
                        .extracting(Category::getName)
                        .containsExactly("더더하위", "하위");
            });
        }

        // Lazy Loading 으로 인해 오동작하는 문제가 있어서 체크를 위해 필요
        @Test
        void 부모_카테고리를_변경할_수_있다3() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            Long childNextCategoryId1 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위 옆",
                    categoryId,
                    childCategoryId,
                    null
            ));
            UpdateCategoryHierarchyCommand command = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(childNextCategoryId1)
                    .memberId(mallangId)
                    .parentId(childCategoryId)
                    .build();

            // when
            categoryService.updateHierarchy(command);

            // then
            transactionHelper.doAssert(() -> {
                Category category = categoryRepository.getById(categoryId);
                assertThat(category.getSortedChildren())
                        .extracting(Category::getName)
                        .containsExactly("하위");
            });
            transactionHelper.doAssert(() -> {
                Category category = categoryRepository.getById(childCategoryId);
                assertThat(category.getSortedChildren())
                        .extracting(Category::getName)
                        .containsExactly("하위 옆");
            });
        }

        // Lazy Loading 으로 인해 오동작하는 문제가 있어서 체크를 위해 필요
        @Test
        void 부모_카테고리를_변경할_수_있다4() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            Long childNextCategoryId1 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위 옆",
                    categoryId,
                    null,
                    childCategoryId
            ));
            UpdateCategoryHierarchyCommand command = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(childNextCategoryId1)
                    .memberId(mallangId)
                    .parentId(childCategoryId)
                    .build();

            // when
            categoryService.updateHierarchy(command);

            // then
            transactionHelper.doAssert(() -> {
                Category category = categoryRepository.getById(categoryId);
                assertThat(category.getSortedChildren())
                        .extracting(Category::getName)
                        .containsExactly("하위");
            });
            transactionHelper.doAssert(() -> {
                Category category = categoryRepository.getById(childCategoryId);
                assertThat(category.getSortedChildren())
                        .extracting(Category::getName)
                        .containsExactly("하위 옆");
            });
        }

        @Test
        void 자신_혹은_자신의_하위_카테고리를_자신의_부모로_만드려는_경우_예외() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            Long childChildCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "더하위1",
                    childCategoryId,
                    null,
                    null
            ));
            UpdateCategoryHierarchyCommand selfParent = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .parentId(categoryId)
                    .build();
            UpdateCategoryHierarchyCommand childToParent = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .parentId(childChildCategoryId)
                    .build();
            UpdateCategoryHierarchyCommand descendantToParent = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .parentId(childChildCategoryId)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.updateHierarchy(selfParent)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
            assertThatThrownBy(() ->
                    categoryService.updateHierarchy(childToParent)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
            assertThatThrownBy(() ->
                    categoryService.updateHierarchy(descendantToParent)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 자신의_카테고리가_아니면_예외() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            UpdateCategoryHierarchyCommand command = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(categoryId)
                    .memberId(otherMemberId)
                    .parentId(null)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityCategoryException.class);
        }

        @Test
        void 다른_사람의_카테고리의_하위_카테고리로_변경하려는_경우_예외() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long otherCategory = categoryService.create(new CreateCategoryCommand(
                    otherMemberId,
                    otherBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            UpdateCategoryHierarchyCommand command = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .parentId(otherCategory)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityCategoryException.class);
        }

        @Test
        void 같은_부모를_가진_형제끼리_이름이_겹치면_예외() {
            // given
            Long 최상위 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long 최상위2 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "자식1",
                    null,
                    최상위,
                    null
            ));
            Long 자식1 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "자식1",
                    최상위,
                    null,
                    null
            ));
            UpdateCategoryHierarchyCommand command = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(최상위2)
                    .memberId(mallangId)
                    .parentId(최상위)
                    .prevId(자식1)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.updateHierarchy(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 루트끼리_이름이_같으면_예외() {
            // given
            Long 최상위 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long 자식 = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    최상위,
                    null,
                    null
            ));
            UpdateCategoryHierarchyCommand command = UpdateCategoryHierarchyCommand.builder()
                    .categoryId(자식)
                    .memberId(mallangId)
                    .parentId(null)
                    .prevId(최상위)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.updateHierarchy(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 제거_시 {

        @Test
        void 하위_카테고리가_있다면_오류() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            DeleteCategoryCommand command = DeleteCategoryCommand.builder()
                    .memberId(mallangId)
                    .categoryId(categoryId)
                    .build();

            // when
            assertThatThrownBy(() ->
                    categoryService.delete(command)
            ).isInstanceOf(ChildCategoryExistException.class);

            // then
            assertThat(categoryRepository.getById(categoryId)).isNotNull();
        }

        @Test
        void 자신의_카테고리가_아니라면_예외() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            DeleteCategoryCommand command = DeleteCategoryCommand.builder()
                    .memberId(otherMemberId)
                    .categoryId(categoryId)
                    .build();
            // when
            assertThatThrownBy(() ->
                    categoryService.delete(command)
            ).isInstanceOf(NoAuthorityCategoryException.class);

            // then
            assertThat(categoryRepository.getById(categoryId)).isNotNull();
        }

        @Test
        void 부모_카테고리의_자식에서_제거된다() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            DeleteCategoryCommand command = DeleteCategoryCommand.builder()
                    .memberId(mallangId)
                    .categoryId(childCategoryId)
                    .build();
            // when
            categoryService.delete(command);

            // then
            assertThatThrownBy(() ->
                    categoryRepository.getById(childCategoryId)
            ).isInstanceOf(NotFoundCategoryException.class);
            transactionHelper.doAssert(() ->
                    assertThat(categoryRepository.getById(categoryId).getSortedChildren()).isEmpty()
            );
        }

        @Test
        void 카테고리_제거_이벤트가_발행된다() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId,
                    mallangBlogName,
                    "최상위",
                    null,
                    null,
                    null
            ));
            DeleteCategoryCommand command = DeleteCategoryCommand.builder()
                    .memberId(mallangId)
                    .categoryId(categoryId)
                    .build();

            // when
            categoryService.delete(command);

            // then
            int count = EventsTestUtils.count(events, CategoryDeletedEvent.class);
            assertThat(count).isEqualTo(1);
        }
    }
}
